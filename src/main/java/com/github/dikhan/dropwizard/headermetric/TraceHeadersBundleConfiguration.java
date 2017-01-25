package com.github.dikhan.dropwizard.headermetric;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class collects all the configuration needed to set up the {@link TraceHeadersBundle} class
 * @author Daniel I. Khan Ramiro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceHeadersBundleConfiguration {

    @Valid
    @NotEmpty
    @JsonProperty
    private String metricPrefix;

    public String getMetricPrefix() {
        return metricPrefix;
    }

    public void setMetricPrefix(String metricPrefix) {
        this.metricPrefix = metricPrefix;
    }
}
