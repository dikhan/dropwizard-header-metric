package com.github.dikhan.dropwizard.headermetric;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.dropwizard.Configuration;

/**
 * A helper class to be able to reuse the code within the {@link TraceHeadersBundle} and
 * {@link com.github.dikhan.dropwizard.headermetric.filters.HeaderMetricFilter} classes
 *
 * @author Daniel I. Khan Ramiro
 */
public class TraceHeadersBundleConfigHelper<T extends Configuration> {

    private final Logger log = LoggerFactory.getLogger(TraceHeadersBundleConfigHelper.class);

    private final T configuration;
    private final TraceHeadersBundle traceHeadersBundle;
    private final MultivaluedMap headersAndValuesToLookUp;

    public TraceHeadersBundleConfigHelper(T configuration, TraceHeadersBundle traceHeadersBundle) {
        this.configuration = configuration;
        this.traceHeadersBundle = traceHeadersBundle;
        this.headersAndValuesToLookUp = createMultivaluedMapFromHeadersToTraceJson();
    }

    public String getHeaderMetricName(String resourceEndPointCanonicalName, String header, String headerValue) {
        String metricPrefix = traceHeadersBundle.getTraceHeadersBundleConfiguration(configuration).getMetricPrefix();
        if (StringUtils.isBlank(metricPrefix)) {
            header = String.format("%s.%s.%s", resourceEndPointCanonicalName, header, headerValue);
        } else {
            header = String.format("%s.%s.%s.%s", resourceEndPointCanonicalName, metricPrefix, header, headerValue);
        }
        return header;
    }

    public MultivaluedMap getHeadersAndValuesToLookUp() {
        return headersAndValuesToLookUp;
    }

    private MultivaluedMap createMultivaluedMapFromHeadersToTraceJson() {
        log.info("Unmarshalling traceHeaders[headersToTraceJson] into a multivalued map");
        MultivaluedHashMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        JsonNode jsonNode = traceHeadersBundle.getTraceHeadersBundleConfiguration(configuration)
                .getHeadersToTraceJsonNode();
        jsonNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey().toLowerCase();
            if (entry.getValue().isArray()) {
                log.info("Key[" + entry.getKey() + "] has an array of values: " + entry.getValue());
                entry.getValue().elements().forEachRemaining(jsonElement -> {
                    String value = jsonElement.asText().toLowerCase();
                    addToMap(headersAndValuesToLookUp, key, value);
                });
            } else {
                String value = entry.getValue().asText().toLowerCase();
                addToMap(headersAndValuesToLookUp, key, value);
            }
        });
        return headersAndValuesToLookUp;
    }

    private void addToMap(MultivaluedHashMap<String, String> headersAndValuesToLookUp, String key, String value) {
        headersAndValuesToLookUp.add(key, value);
        log.info("Added new header from headersToTraceJson property Json [" + key + ": " + value + "]");
    }

    public String getResourceEndPointCanonicalName(String classEndPointCanonicalName, String annotationNameValue) {
        return classEndPointCanonicalName + "." + annotationNameValue;
    }
}
