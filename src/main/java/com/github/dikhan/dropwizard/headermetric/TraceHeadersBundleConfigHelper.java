package com.github.dikhan.dropwizard.headermetric;

import io.dropwizard.Configuration;
import org.apache.commons.lang3.StringUtils;

/**
 * A helper class to be able to reuse the code within the {@link TraceHeadersBundle} and
 * {@link com.github.dikhan.dropwizard.headermetric.filters.HeaderMetricFilter} classes
 *
 * @author Daniel I. Khan Ramiro
 */
public class TraceHeadersBundleConfigHelper<T extends Configuration> {

    private final T configuration;
    private final TraceHeadersBundle traceHeadersBundle;

    public TraceHeadersBundleConfigHelper(T configuration, TraceHeadersBundle traceHeadersBundle) {
        this.configuration = configuration;
        this.traceHeadersBundle = traceHeadersBundle;
    }

    public String getHeaderMetricName(String header, String headerValue) {
        String metricPrefix = traceHeadersBundle.getTraceHeadersBundleConfiguration(configuration).getMetricPrefix();
        if(StringUtils.isBlank(metricPrefix)) {
            header = String.format("%s-%s", header, headerValue);
        } else {
            header = String.format("%s-%s-%s", metricPrefix, header, headerValue);
        }
        return header;
    }
}
