package com.github.dikhan.dropwizard.headermetric.features;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.github.dikhan.dropwizard.headermetric.annotations.HeaderMetric;
import com.github.dikhan.dropwizard.headermetric.filters.HeaderMetricFilter;

@Provider
public class HeaderMetricFeature implements DynamicFeature {

    private MultivaluedMap<String, String> headersAndValuesToLookUp;

    public HeaderMetricFeature(MultivaluedMap<String, String> headersAndValuesToLookUp) {
        this.headersAndValuesToLookUp = headersAndValuesToLookUp;
    }

    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().getAnnotation(HeaderMetric.class) != null) {
            context.register(new HeaderMetricFilter(headersAndValuesToLookUp));
        }
    }
}
