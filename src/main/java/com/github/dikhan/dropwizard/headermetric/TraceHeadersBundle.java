package com.github.dikhan.dropwizard.headermetric;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 *  TraceHeadersBundle that provides an easy to use and hassle-free {@link io.dropwizard.ConfiguredBundle}
 *  on top of DropWizard.
 * @param <T> Config class that should provide implementation for {@link TraceHeadersBundleConfiguration}
 *
 * @author Daniel I. Khan Ramiro
 */
public abstract class TraceHeadersBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger log = LoggerFactory.getLogger(TraceHeadersBundle.class);

    private final MultivaluedMap<String, String> headersAndValuesToLookUp;
    private final MetricRegistry metricRegistry;

    public TraceHeadersBundle(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
        this.headersAndValuesToLookUp = lowerCaseHeadersAndValuesToLookUp(headersAndValuesToLookUp);
        this.metricRegistry = metricRegistry;
    }

    public void initialize(Bootstrap<?> bootstrap) {
    }

    public void run(T configuration, Environment environment) throws Exception {
        TraceHeadersBundleConfiguration traceHeadersBundleConfiguration = getTraceHeadersBundleConfiguration(configuration);
        if (traceHeadersBundleConfiguration == null) {
            throw new IllegalStateException("You need to provide an instance of TraceHeadersBundleConfiguration");
        }
        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = new TraceHeadersBundleConfigHelper(configuration, this);
        environment.jersey().register(new HeaderMetricFeature(traceHeadersBundleConfigHelper, headersAndValuesToLookUp, metricRegistry));
        registerHeaderMetrics(traceHeadersBundleConfigHelper, headersAndValuesToLookUp, metricRegistry);
    }

    private void registerHeaderMetrics(TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper, MultivaluedMap<String, String> headersAndValuesToLookUp,
            MetricRegistry metricRegistry) {
        for(Map.Entry<String, List<String>> headerToRegister: headersAndValuesToLookUp.entrySet()) {
            for(String headerValue: headerToRegister.getValue()) {
                String header = traceHeadersBundleConfigHelper.getHeaderMetricName(headerToRegister.getKey(), headerValue);
                metricRegistry.register(header, new Counter());
                log.info("New Header Metric registered -> {}", header);
            }
        }
    }

    /**
     * This method is necessary to avoid potential issues whereby the user configures the headers to look up in
     * upper case and the headers coming from the request are lower case
     * @param headersAndValues map containing the headers and values to be tracked
     * @return multivalued map containing headers and values in lower case
     */
    private MultivaluedMap<String, String> lowerCaseHeadersAndValuesToLookUp(MultivaluedMap<String, String> headersAndValues) {
        MultivaluedHashMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        for(Map.Entry<String, List<String>> headerToRegister: headersAndValues.entrySet()) {
            String headerKeyToLookUp = headerToRegister.getKey().toLowerCase();
            for(String headerKeyValueToLookUp : headerToRegister.getValue()) {
                headersAndValuesToLookUp.add(headerKeyToLookUp, headerKeyValueToLookUp.toLowerCase());
            }
        }
        return headersAndValuesToLookUp;
    }

    protected abstract TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(T configuration);

}
