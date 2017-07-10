package com.ewolff.microservice.order.prometheus;

import java.util.Collection;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.SpringBootMetricsCollector;

@Configuration
public class PrometheusConfiguration {

	@Bean
	public SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
		SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(publicMetrics);
		springBootMetricsCollector.register();
		return springBootMetricsCollector;
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		DefaultExports.initialize();
		return new ServletRegistrationBean(new MetricsServlet(), "/prometheus");
	}

}
