package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.Constants.HEADER_METRIC_PREFIX;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderMetricBundle implements Bundle {

    private static final Logger log = LoggerFactory.getLogger(HeaderMetricBundle.class);

    private final MultivaluedMap<String, String> headersAndValuesToLookUp;
    private final MetricRegistry metricRegistry;

    public HeaderMetricBundle(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
        this.headersAndValuesToLookUp = new MultivaluedHashMap<>();
        this.headersAndValuesToLookUp.putAll(headersAndValuesToLookUp);
        this.metricRegistry = metricRegistry;
    }

    public void initialize(Bootstrap<?> bootstrap) {
    }

    public void run(Environment environment) {
        environment.jersey().register(new HeaderMetricFeature(headersAndValuesToLookUp, metricRegistry));
        registerMetrics(headersAndValuesToLookUp, metricRegistry);
    }

    private void registerMetrics(MultivaluedMap<String, String> headersAndValuesToLookUp,
            MetricRegistry metricRegistry) {
        for(Map.Entry<String, List<String>> headerToRegister: headersAndValuesToLookUp.entrySet()) {
            for(String headerValue: headerToRegister.getValue()) {
                String header = String.format("%s-%s-%s", HEADER_METRIC_PREFIX, headerToRegister.getKey().toLowerCase(), headerValue.toLowerCase());
                metricRegistry.register(header, new Counter());
                log.info("New Header Metric registered -> {}", header);
            }
        }
    }

}
