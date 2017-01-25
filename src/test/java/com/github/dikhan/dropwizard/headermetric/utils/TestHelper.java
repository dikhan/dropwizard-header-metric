package com.github.dikhan.dropwizard.headermetric.utils;

import javax.ws.rs.core.MultivaluedMap;

import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.TraceHeadersApplicationConfiguration;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundle;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfiguration;

/**
 * @author Daniel I. Khan Ramiro
 */
public class TestHelper {

    public static final String HEADER_METRIC_PREFIX = "HeaderMetric";

    public static TraceHeadersApplicationConfiguration setUpTraceHeadersApplicationConfiguration(String metricPrefix) {
        TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration = new TraceHeadersApplicationConfiguration();
        TraceHeadersBundleConfiguration traceHeadersBundleConfiguration = new TraceHeadersBundleConfiguration();
        traceHeadersBundleConfiguration.setMetricPrefix(metricPrefix);
        traceHeadersApplicationConfiguration.setTraceHeadersBundleConfiguration(traceHeadersBundleConfiguration);
        return traceHeadersApplicationConfiguration;
    }

    public static TraceHeadersBundle<TraceHeadersApplicationConfiguration> setUpTraceHeadersBundle(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
        return new TraceHeadersBundle<TraceHeadersApplicationConfiguration>(headersAndValuesToLookUp, metricRegistry) {
            @Override
            protected TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(TraceHeadersApplicationConfiguration configuration) {
                return configuration.getTraceHeadersBundleConfiguration();
            }
        };
    }

    public static TraceHeadersBundleConfigHelper setUpTraceHeadersBundleConfigHelper(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
        TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration = setUpTraceHeadersApplicationConfiguration(HEADER_METRIC_PREFIX);
        TraceHeadersBundle traceHeadersBundle = setUpTraceHeadersBundle(headersAndValuesToLookUp, metricRegistry);
        return new TraceHeadersBundleConfigHelper<>(traceHeadersApplicationConfiguration, traceHeadersBundle);
    }
}
