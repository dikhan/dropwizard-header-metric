package com.github.dikhan.dropwizard.headermetric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 *  TraceHeadersBundle that provides an easy to use and hassle-free {@link io.dropwizard.ConfiguredBundle}
 *  on top of DropWizard.
 * @param <T> Config class that should provide implementation for {@link TraceHeadersBundleConfiguration}
 *
 * @author Daniel I. Khan Ramiro
 */
public abstract class TraceHeadersBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger log = LoggerFactory.getLogger(TraceHeadersBundle.class);

    private final MetricRegistry metricRegistry;

    public TraceHeadersBundle(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void initialize(Bootstrap<?> bootstrap) {
    }

    public void run(T configuration, Environment environment) throws Exception {
        TraceHeadersBundleConfiguration traceHeadersBundleConfiguration = getTraceHeadersBundleConfiguration(configuration);
        if (traceHeadersBundleConfiguration == null) {
            throw new IllegalStateException("You need to provide an instance of TraceHeadersBundleConfiguration");
        }
        TraceHeadersBundleConfigHelper<T> traceHeadersBundleConfigHelper = new TraceHeadersBundleConfigHelper<>(configuration, this);
        environment.jersey().register(new HeaderMetricFeature(traceHeadersBundleConfigHelper, metricRegistry));
    }

    protected abstract TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(T configuration);

}
