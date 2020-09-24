package com.ewolff.microservice.order.clients;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CatalogClient {

	private final Logger log = LoggerFactory.getLogger(CatalogClient.class);

	public static class ItemPagedResources extends PagedModel<Item> {

	}

	private RestTemplate restTemplate;
	private String catalogServiceHost;
	private long catalogServicePort;
	private boolean useRibbon;
	private LoadBalancerClient loadBalancer;

	@Autowired
	public CatalogClient(@Value("${catalog.service.host:catalog}") String catalogServiceHost,
			@Value("${catalog.service.port:8080}") long catalogServicePort,
			@Value("${spring.cloud.consul.ribbon.enabled:false}") boolean useRibbon,
			@Autowired RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
		this.catalogServiceHost = catalogServiceHost;
		this.catalogServicePort = catalogServicePort;
		this.useRibbon = useRibbon;
	}

	@Autowired(required = false)
	public void setLoadBalancer(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	protected RestTemplate getRestTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converter.setObjectMapper(mapper);

		return new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
	}

	public double price(long itemId) {
		return getOne(itemId).getPrice();
	}

	public Collection<Item> findAll() {
		PagedModel<Item> pagedResources = getRestTemplate().getForObject(catalogURL(), ItemPagedResources.class);
		return pagedResources.getContent();
	}

	private String catalogURL() {
		String url;
		if (useRibbon) {
			ServiceInstance instance = loadBalancer.choose("CATALOG");
			url = String.format("http://%s:%s/catalog/", instance.getHost(), instance.getPort());
		} else {
			url = String.format("http://%s:%s/catalog/", catalogServiceHost, catalogServicePort);
		}
		log.trace("Catalog: URL {} ", url);
		return url;
	}

	public Item getOne(long itemId) {
		return restTemplate.getForObject(catalogURL() + itemId, Item.class);
	}

}
