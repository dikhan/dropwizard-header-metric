package com.github.dikhan.dropwizard.headermetric.filters;

import static com.github.dikhan.dropwizard.headermetric.Constants.HEADER_METRIC_PREFIX;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

@Provider
public class HeaderMetricFilter implements ContainerRequestFilter {

    private MultivaluedMap<String, String> headersAndValuesToLookUp;
    private final MetricRegistry metricRegistry;

    public HeaderMetricFilter(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry) {
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
                        Counter counter = metricRegistry.counter(HEADER_METRIC_PREFIX + "-" + searchedHeader + "-" + searchedHeaderValue);
                        counter.inc();
                        break;
                    }
                }
            }
        }
    }
}
