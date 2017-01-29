package com.github.dikhan.dropwizard.headermetric.features;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;
import com.github.dikhan.dropwizard.headermetric.annotations.TraceConfiguredHeaders;
import com.github.dikhan.dropwizard.headermetric.filters.HeaderMetricFilter;

@Provider
public class HeaderMetricFeature implements DynamicFeature {

    private static final Logger log = LoggerFactory.getLogger(HeaderMetricFeature.class);

    private final TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper;
    private final MetricRegistry metricRegistry;

    public HeaderMetricFeature(TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper,
            MetricRegistry metricRegistry) {
        this.traceHeadersBundleConfigHelper = traceHeadersBundleConfigHelper;
        this.metricRegistry = metricRegistry;
    }

    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        TraceConfiguredHeaders matchedResourceTraceConfiguredHeadersAnnotation = resourceInfo.getResourceMethod()
                .getAnnotation(TraceConfiguredHeaders.class);
        if (matchedResourceTraceConfiguredHeadersAnnotation != null) {
            String endPointHit = matchedResourceTraceConfiguredHeadersAnnotation.name();
            context.register(new HeaderMetricFilter(endPointHit,
                    traceHeadersBundleConfigHelper, metricRegistry));
            registerHeaderMetrics(endPointHit, traceHeadersBundleConfigHelper, metricRegistry);
        }
    }

    private void registerHeaderMetrics(String endPointHit,
            TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper, MetricRegistry metricRegistry) {
        MultivaluedMap<String, String> headersAndValuesToLookUp = traceHeadersBundleConfigHelper.getHeadersAndValuesToLookUp();
        headersAndValuesToLookUp.entrySet().forEach(
                headerToRegister -> {
                    for (String headerValue : headerToRegister.getValue()) {
                        String header = traceHeadersBundleConfigHelper.getHeaderMetricName(endPointHit, headerToRegister.getKey(),
                                headerValue);
                        metricRegistry.register(header, new Counter());
                        log.info("New Header Metric registered -> {}", header);
                    }
                });
    }

}
