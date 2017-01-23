package com.github.dikhan.dropwizard;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.github.dikhan.dropwizard.headermetric.HeaderMetricBundle;
import com.github.dikhan.dropwizard.headermetric.resources.HelloWorldWithHeaderMetricsResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DropWizardApplication extends Application<DropWizardApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new DropWizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "header-metric-application";
    }

    @Override
    public void initialize(Bootstrap<DropWizardApplicationConfiguration> bootstrap) {
        MultivaluedMap<String, String> headersToMeasure = getHeadersToMeasure();
        bootstrap.addBundle(new HeaderMetricBundle(headersToMeasure, bootstrap.getMetricRegistry()));
    }

    @Override
    public void run(DropWizardApplicationConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new HelloWorldWithHeaderMetricsResource());
    }

    private MultivaluedMap<String, String> getHeadersToMeasure() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("X-CUSTOM-HEADER", "X-CUSTOM-HEADER-VALUE");
        return headers;
    }

}
