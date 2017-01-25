package com.github.dikhan.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfiguration;

import io.dropwizard.Configuration;

public class TraceHeadersApplicationConfiguration extends Configuration {

    @JsonProperty("traceHeaders")
    private TraceHeadersBundleConfiguration traceHeadersBundleConfiguration;

    public TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration() {
        return traceHeadersBundleConfiguration;
    }

    public void setTraceHeadersBundleConfiguration(TraceHeadersBundleConfiguration traceHeadersBundleConfiguration) {
        this.traceHeadersBundleConfiguration = traceHeadersBundleConfiguration;
    }
}
