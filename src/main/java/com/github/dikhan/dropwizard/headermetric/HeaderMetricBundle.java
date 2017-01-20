package com.github.dikhan.dropwizard.headermetric;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HeaderMetricBundle implements Bundle {

    private MultivaluedMap<String, String> headersAndValuesToLookUp;

    public HeaderMetricBundle(MultivaluedMap<String, String> headersAndValuesToLookUp) {
        this.headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.putAll(headersAndValuesToLookUp);
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }

    public void run(Environment environment) {
        environment.jersey().register(new HeaderMetricFeature(headersAndValuesToLookUp));
    }
}
