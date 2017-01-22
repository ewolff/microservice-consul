package com.ewolff.microservice.order.logic;

import com.ewolff.microservice.order.OrderApp;
import com.ewolff.microservice.order.clients.CatalogClient;
import com.ewolff.microservice.order.clients.Customer;
import com.ewolff.microservice.order.clients.CustomerClient;
import com.ewolff.microservice.order.clients.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApp.class, webEnvironment = DEFINED_PORT)
@ActiveProfiles("test")
public class OrderWebIntegrationTest {

	private RestTemplate restTemplate = new RestTemplate();

	@LocalServerPort
	private long serverPort;

	@Autowired
	private CatalogClient catalogClient;

	@Autowired
	private CustomerClient customerClient;

	@Autowired
	private OrderRepository orderRepository;

	private Item item;

	private Customer customer;

	@Before
	public void setup() {
		item = catalogClient.findAll().iterator().next();
		customer = customerClient.findAll().iterator().next();
		assertEquals("Eberhard", customer.getFirstname());
	}

	@Test
	public void IsOrderListReturned() {
		try {
			Iterable<Order> orders = orderRepository.findAll();
			assertTrue(StreamSupport
					.stream(orders.spliterator(), false)
					.noneMatch(
							o -> (o.getCustomerId() == customer.getCustomerId())));
			ResponseEntity<String> resultEntity = restTemplate.getForEntity(
					orderURL(), String.class);
			assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
			String orderList = resultEntity.getBody();
			assertFalse(orderList.contains("Eberhard"));
			Order order = new Order(customer.getCustomerId());
			order.addLine(42, item.getItemId());
			orderRepository.save(order);
			orderList = restTemplate.getForObject(orderURL(), String.class);
			assertTrue(orderList.contains("Eberhard"));
		} finally {
			orderRepository.deleteAll();
		}
	}

	private String orderURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsOrderFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				orderURL() + "/form", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<form"));
	}

	@Test
	@Transactional
	public void IsSubmittedOrderSaved() {
		long before = orderRepository.count();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("submit", "");
		map.add("customerId", Long.toString(customer.getCustomerId()));
		map.add("orderLine[0].itemId", Long.toString(item.getItemId()));
		map.add("orderLine[0].count", "42");
		URI uri = restTemplate.postForLocation(orderURL(), map, String.class);
		UriTemplate uriTemplate = new UriTemplate(orderURL() + "/{id}");
		assertEquals(before + 1, orderRepository.count());
	}
}
