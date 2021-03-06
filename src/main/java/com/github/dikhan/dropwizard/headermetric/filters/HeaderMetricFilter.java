package com.github.dikhan.dropwizard.headermetric.filters;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;

@Provider
public class HeaderMetricFilter implements ContainerRequestFilter {

    private final String endPointHit;
    private final TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper;
    private final MetricRegistry metricRegistry;

    public HeaderMetricFilter(String endPointHit, TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper,
            MetricRegistry metricRegistry) {
        this.endPointHit = endPointHit;
        this.traceHeadersBundleConfigHelper = traceHeadersBundleConfigHelper;
        this.metricRegistry = metricRegistry;
    }

    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headersAndValuesToLookUp = traceHeadersBundleConfigHelper
                .getHeadersAndValuesToLookUp();
        final MultivaluedMap<String, String> requestContextHeaders = requestContext.getHeaders();
        headersAndValuesToLookUp.entrySet().forEach(headerToLookUp -> {
            String searchedHeader = headerToLookUp.getKey();
            List<String> matchedKeyValues = requestContextHeaders.get(searchedHeader);
            if (matchedKeyValues != null) {
                final List<String> matchedKeyValuesLowerCase = matchedKeyValues.stream().map(String::toLowerCase)
                        .collect(Collectors.toList());
                List<String> headerToLookUpValues = headerToLookUp.getValue();
                headerToLookUpValues.forEach(
                        searchedValue -> increaseCounterIfMetricMatch(matchedKeyValuesLowerCase, searchedHeader,
                                searchedValue));
            }
        });
    }

    /**
     * Method that increases the counter of the header metric if there is a match.
     * <p>
     * When a request contains multiple values for a given header, the multivalued map does not include multiple indexes
     * for the different values; rather the values are all stored in the first index concatenated.
     * E,g: x-custom-header-value-1,x-custom-header-value-2.
     * Hence the need to look up the first index and search for matches, this is a special case that needs to be covered.
     */
    private void increaseCounterIfMetricMatch(List<String> matchedKeyValuesLowerCase, String searchedHeader,
            String searchedValue) {
        if (matchedKeyValuesLowerCase.get(0).contains(searchedValue) || matchedKeyValuesLowerCase
                .contains(searchedValue)) {
            String metricName = traceHeadersBundleConfigHelper
                    .getHeaderMetricName(endPointHit, searchedHeader, searchedValue);
            Counter counter = metricRegistry.counter(metricName);
            counter.inc();
        }
    }
}
