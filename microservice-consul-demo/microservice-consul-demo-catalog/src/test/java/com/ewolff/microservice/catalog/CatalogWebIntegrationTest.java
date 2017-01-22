package com.ewolff.microservice.catalog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CatalogApp.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class CatalogWebIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;

	@LocalServerPort
	private int serverPort;

	private Item iPodNano;

	private RestTemplate restTemplate;

	@Before
	public void setup() {
		iPodNano = itemRepository.findByName("iPod nano").get(0);
		restTemplate = new RestTemplate();
	}

	@Test
	public void IsItemReturnedAsHTML() {
		String url = catalogURL() + "/" + iPodNano.getId() + ".html";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
	}

	private String catalogURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsItemReturnedAsJON() {
		String url = catalogURL() + "/catalog/" + iPodNano.getId();
		Item body = getForMediaType(Item.class, MediaType.APPLICATION_JSON, url);

		assertThat(body, equalTo(iPodNano));
	}

	@Test
	public void FormReturned() {
		String url = catalogURL() + "/searchForm.html";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("<form"));
		assertThat(body, containsString("<div>"));
	}

	@Test
	public void SearchWorks() {
		String url = catalogURL() + "/searchByName.html?query=iPod";
		String body = restTemplate.getForObject(url, String.class);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
	}

	private <T> T getForMediaType(Class<T> value, MediaType mediaType,
			String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(mediaType));

		HttpEntity<String> entity = new HttpEntity<String>("parameters",
				headers);

		ResponseEntity<T> resultEntity = restTemplate.exchange(url,
				HttpMethod.GET, entity, value);

		return resultEntity.getBody();
	}

}
