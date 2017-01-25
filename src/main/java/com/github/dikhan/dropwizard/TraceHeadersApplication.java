package com.github.dikhan.dropwizard;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

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
        MultivaluedMap<String, String> headersToMeasure = getHeadersToMeasure();
        bootstrap.addBundle(new TraceHeadersBundle<TraceHeadersApplicationConfiguration>(headersToMeasure, bootstrap.getMetricRegistry()) {
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

    /**
     * This method serves as an example on how to pre-populate the map that will be used in the bundle
     * to register all the headers as metrics to be able to monitor them and report them should the user
     * has specified any reporter in the metrics configuration in the yml file.
     * These can even be injected by a DI framework such as GoogleGuice
     * @return map containing the headers to look up when receiving a new request
     */
    private MultivaluedMap<String, String> getHeadersToMeasure() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("X-CUSTOM-HEADER", "X-CUSTOM-HEADER-VALUE");
        return headers;
    }

}
