package com.github.dikhan.dropwizard.headermetric.utils;

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
    public static final String HEADERS_TO_TRACE_JSON = "{\"request_header_1\": [\"request_header_1_value_1\", \"request_header_1_value_2\"], \"request_header_2\": \"request_header_2_value\"}";

    public static TraceHeadersApplicationConfiguration setUpTraceHeadersApplicationConfiguration(String metricPrefix, String headersToTraceJson) {
        TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration = new TraceHeadersApplicationConfiguration();
        TraceHeadersBundleConfiguration traceHeadersBundleConfiguration = new TraceHeadersBundleConfiguration();
        traceHeadersBundleConfiguration.setMetricPrefix(metricPrefix);
        traceHeadersBundleConfiguration.setHeadersToTraceJson(headersToTraceJson);
        traceHeadersApplicationConfiguration.setTraceHeadersBundleConfiguration(traceHeadersBundleConfiguration);
        return traceHeadersApplicationConfiguration;
    }

    public static TraceHeadersBundle<TraceHeadersApplicationConfiguration> setUpTraceHeadersBundle(MetricRegistry metricRegistry) {
        return new TraceHeadersBundle<TraceHeadersApplicationConfiguration>(metricRegistry) {
            @Override
            protected TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(TraceHeadersApplicationConfiguration configuration) {
                return configuration.getTraceHeadersBundleConfiguration();
            }
        };
    }

    public static TraceHeadersBundleConfigHelper setUpTraceHeadersBundleConfigHelper(String headersToTraceJson, MetricRegistry metricRegistry) {
        TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration = setUpTraceHeadersApplicationConfiguration(HEADER_METRIC_PREFIX, headersToTraceJson);
        TraceHeadersBundle traceHeadersBundle = setUpTraceHeadersBundle(metricRegistry);
        return new TraceHeadersBundleConfigHelper<>(traceHeadersApplicationConfiguration, traceHeadersBundle);
    }
}
