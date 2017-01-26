package com.github.dikhan.dropwizard.headermetric;

import java.io.IOException;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class collects all the configuration needed to set up the {@link TraceHeadersBundle} class
 * @author Daniel I. Khan Ramiro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceHeadersBundleConfiguration {

    private final Logger log = LoggerFactory.getLogger(TraceHeadersBundleConfiguration.class);

    @Valid
    private String metricPrefix;

    @Valid
    private String headersToTraceJson;

    private JsonNode headersToTraceJsonNode;

    public String getMetricPrefix() {
        return metricPrefix;
    }

    public void setMetricPrefix(String metricPrefix) {
        this.metricPrefix = metricPrefix;
    }

    public String getHeadersToTraceJson() {
        return headersToTraceJson;
    }

    public void setHeadersToTraceJson(String headersToTraceJson) {
        this.headersToTraceJson = headersToTraceJson.isEmpty() ? "{}" : headersToTraceJson;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            headersToTraceJsonNode = mapper.readTree(this.headersToTraceJson);
            log.info("Valid headersToTraceJsonNode:" + headersToTraceJsonNode);
        } catch (IOException e) {
            throw new RuntimeException("headersToTraceJson property is not a valid JSON object");
        }
    }

    public JsonNode getHeadersToTraceJsonNode() {
        return headersToTraceJsonNode;
    }
}
