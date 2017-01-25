package com.github.dikhan.dropwizard.headermetric.filters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;

@Provider
public class HeaderMetricFilter implements ContainerRequestFilter {

    private final TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper;
    private MultivaluedMap<String, String> headersAndValuesToLookUp;
    private final MetricRegistry metricRegistry;

    public HeaderMetricFilter(TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper, MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
        this.traceHeadersBundleConfigHelper = traceHeadersBundleConfigHelper;
        this.headersAndValuesToLookUp = headersAndValuesToLookUp;
        this.metricRegistry = metricRegistry;
    }

    public void filter(ContainerRequestContext requestContext) throws IOException {
        final MultivaluedMap<String, String> requestContextHeaders = requestContext.getHeaders();
        for(Map.Entry<String, List<String>> headerToLookUp: headersAndValuesToLookUp.entrySet()) {
            String searchedHeader = headerToLookUp.getKey().toLowerCase();
            List<String> matchedKeyValues = requestContextHeaders.get(searchedHeader);
            if(matchedKeyValues != null) {
                List<String> headerToLookUpValues = headerToLookUp.getValue();
                for(String headerValueToLookUp: headerToLookUpValues) {
                    String searchedHeaderValue = headerValueToLookUp.toLowerCase();
                    if(matchedKeyValues.contains(searchedHeaderValue)) {
                        String metricName = traceHeadersBundleConfigHelper.getHeaderMetricName(searchedHeader, searchedHeaderValue);
                        Counter counter = metricRegistry.counter(metricName);
                        counter.inc();
                        break;
                    }
                }
            }
        }
    }
}
