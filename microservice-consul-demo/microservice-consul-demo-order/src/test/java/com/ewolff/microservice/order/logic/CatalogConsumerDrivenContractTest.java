package com.ewolff.microservice.order.logic;

import com.ewolff.microservice.order.OrderApp;
import com.ewolff.microservice.order.clients.CatalogClient;
import com.ewolff.microservice.order.clients.Item;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApp.class, webEnvironment = DEFINED_PORT)
@ActiveProfiles("test")
public class CatalogConsumerDrivenContractTest {

	@Autowired
	CatalogClient catalogClient;

	@Test
	public void testFindAll() {
		Collection<Item> result = catalogClient.findAll();
		assertEquals(
				1,
				result.stream()
						.filter(i -> (i.getName().equals("iPod")
								&& i.getPrice() == 42.0 && i.getItemId() == 1))
						.count());
	}

	@Test
	public void testGetOne() {
		Collection<Item> allItems = catalogClient.findAll();
		Long id = allItems.iterator().next().getItemId();
		Item result = catalogClient.getOne(id);
		assertEquals(id.longValue(), result.getItemId());
	}

}
