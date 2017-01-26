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
    private final MetricRegistry metricRegistry;

    public HeaderMetricFilter(TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper, MetricRegistry metricRegistry) {
        this.traceHeadersBundleConfigHelper = traceHeadersBundleConfigHelper;
        this.metricRegistry = metricRegistry;
    }

    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headersAndValuesToLookUp = traceHeadersBundleConfigHelper.getMultivaluedMapFromHeadersToTraceJson();
        final MultivaluedMap<String, String> requestContextHeaders = requestContext.getHeaders();
        for(Map.Entry<String, List<String>> headerToLookUp: headersAndValuesToLookUp.entrySet()) {
            String searchedHeader = headerToLookUp.getKey();
            List<String> matchedKeyValues = requestContextHeaders.get(searchedHeader);
            if(matchedKeyValues != null) {
                List<String> headerToLookUpValues = headerToLookUp.getValue();
                for(String searchedValue: headerToLookUpValues) {
                    // When a request contains multiple values for a given header, the multivalued map does not
                    // include multiple indexes for the different values; rather the values are all stored in the first
                    // index concatenated. E,g: x-custom-header-value-1,x-custom-header-value-2. Hence the need to
                    // look up the first index and search for matches, this is a special case that needs to be covered.
                    if(matchedKeyValues.get(0).contains(searchedValue) || matchedKeyValues.contains(
                            searchedValue)) {
                        String metricName = traceHeadersBundleConfigHelper.getHeaderMetricName(searchedHeader,
                                searchedValue);
                        Counter counter = metricRegistry.counter(metricName);
                        counter.inc();
                    }
                }
            }
        }
    }
}
