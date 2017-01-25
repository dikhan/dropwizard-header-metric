package com.github.dikhan.dropwizard;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundle;
import com.github.dikhan.dropwizard.headermetric.resources.HelloWorldResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TraceHeadersApplication extends Application<TraceHeadersApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new TraceHeadersApplication().run(args);
    }

    @Override
    public String getName() {
        return "header-metric-application";
    }

    @Override
    public void initialize(Bootstrap<TraceHeadersApplicationConfiguration> bootstrap) {
        MultivaluedMap<String, String> headersToMeasure = getHeadersToMeasure();
        bootstrap.addBundle(new TraceHeadersBundle(headersToMeasure, bootstrap.getMetricRegistry()));
    }

    @Override
    public void run(TraceHeadersApplicationConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new HelloWorldResource());
    }

    private MultivaluedMap<String, String> getHeadersToMeasure() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("X-CUSTOM-HEADER", "X-CUSTOM-HEADER-VALUE");
        return headers;
    }

}
