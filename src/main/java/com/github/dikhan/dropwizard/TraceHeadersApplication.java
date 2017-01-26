package com.github.dikhan.dropwizard;

import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundle;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfiguration;
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
        bootstrap.addBundle(new TraceHeadersBundle<TraceHeadersApplicationConfiguration>(bootstrap.getMetricRegistry()) {
            @Override
            protected TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(TraceHeadersApplicationConfiguration configuration) {
                return configuration.getTraceHeadersBundleConfiguration();
            }
        });
    }

    @Override
    public void run(TraceHeadersApplicationConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new HelloWorldResource());
    }

}
