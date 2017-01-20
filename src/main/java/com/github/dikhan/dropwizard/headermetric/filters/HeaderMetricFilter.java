package com.github.dikhan.dropwizard.headermetric.filters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class HeaderMetricFilter implements ContainerRequestFilter {

    private MultivaluedMap<String, String> headersAndValuesToLookUp;

    public HeaderMetricFilter(MultivaluedMap<String, String> headersAndValuesToLookUp) {
        this.headersAndValuesToLookUp = headersAndValuesToLookUp;
    }

    public void filter(ContainerRequestContext requestContext) throws IOException {
        final MultivaluedMap<String, String> requestContextHeaders = requestContext.getHeaders();
        for(Map.Entry<String, List<String>> headerToLookUp: headersAndValuesToLookUp.entrySet()) {
            List<String> matchedKeyValues = requestContextHeaders.get(headerToLookUp.getKey());
            if(matchedKeyValues != null) {
                List<String> headerToLookUpValues = headerToLookUp.getValue();
                for(String headerValueToLookUp: headerToLookUpValues) {
                    if(matchedKeyValues.contains(headerValueToLookUp)) {
                        // Send metric to the reporter
                        break;
                    }
                }
                break;
            }
        }
    }
}
