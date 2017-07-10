package com.ewolff.microservice.order.prometheus;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.Collector;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

/**
 * The original MetricServlet duplicates metrics. This is a hacky workaround.
 *
 */
public class MetricsServletFix extends HttpServlet {
	private CollectorRegistry registry;

	public MetricsServletFix() {
		this(CollectorRegistry.defaultRegistry);
	}

	public MetricsServletFix(CollectorRegistry registry) {
		this.registry = registry;
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType(TextFormat.CONTENT_TYPE_004);

		Writer writer = resp.getWriter();
		Enumeration<Collector.MetricFamilySamples> metricsWithDuplicates = registry.metricFamilySamples();
		List<String> names = new ArrayList<String>();
		List<Collector.MetricFamilySamples> metricsWithoutDuplicates = new ArrayList<Collector.MetricFamilySamples>();
		while (metricsWithDuplicates.hasMoreElements()) {
			MetricFamilySamples metric = metricsWithDuplicates.nextElement();
			if (!names.contains(metric.name)) {
				metricsWithoutDuplicates.add(metric);
				names.add(metric.name);
			}
		}

		TextFormat.write004(writer, Collections.enumeration(metricsWithoutDuplicates));
		writer.flush();
		writer.close();
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
