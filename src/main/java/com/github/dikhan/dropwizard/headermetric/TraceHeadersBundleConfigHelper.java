package com.github.dikhan.dropwizard.headermetric;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    private MultivaluedHashMap<String, String> headersAndValuesToLookUp;

    public TraceHeadersBundleConfigHelper(T configuration, TraceHeadersBundle traceHeadersBundle) {
        this.configuration = configuration;
        this.traceHeadersBundle = traceHeadersBundle;
    }

    public String getHeaderMetricName(String header, String headerValue) {
        String metricPrefix = traceHeadersBundle.getTraceHeadersBundleConfiguration(configuration).getMetricPrefix();
        if(StringUtils.isBlank(metricPrefix)) {
            header = String.format("%s-%s", header, headerValue);
        } else {
            header = String.format("%s-%s-%s", metricPrefix, header, headerValue);
        }
        return header;
    }

    public MultivaluedMap<String, String> getMultivaluedMapFromHeadersToTraceJson() {
        if(headersAndValuesToLookUp == null) {
            MultivaluedHashMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
            JsonNode jsonNode = traceHeadersBundle.getTraceHeadersBundleConfiguration(configuration).getHeadersToTraceJsonNode();
            jsonNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey().toLowerCase();
                if(entry.getValue().isArray()) {
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
            this.headersAndValuesToLookUp = headersAndValuesToLookUp;
        }
        return headersAndValuesToLookUp;
    }

    private void addToMap(MultivaluedHashMap<String, String> headersAndValuesToLookUp, String key, String value) {
        headersAndValuesToLookUp.add(key, value);
        log.info("Added new header from Json [" + key + ": " + value + "]");
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
}
