package com.github.dikhan.dropwizard.headermetric.features;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;
import com.github.dikhan.dropwizard.headermetric.annotations.TraceConfiguredHeaders;
import com.github.dikhan.dropwizard.headermetric.filters.HeaderMetricFilter;

@Provider
public class HeaderMetricFeature implements DynamicFeature {

    private final TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper;
    private final MetricRegistry metricRegistry;

    public HeaderMetricFeature(TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper, MetricRegistry metricRegistry) {
        this.traceHeadersBundleConfigHelper = traceHeadersBundleConfigHelper;
        this.metricRegistry = metricRegistry;
    }

    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().getAnnotation(TraceConfiguredHeaders.class) != null) {
            context.register(new HeaderMetricFilter(traceHeadersBundleConfigHelper, metricRegistry));
        }
    }
}
