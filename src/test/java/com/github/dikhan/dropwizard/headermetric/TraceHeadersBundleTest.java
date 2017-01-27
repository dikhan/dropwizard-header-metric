package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.TraceHeadersApplicationConfiguration;
import com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;

public class TraceHeadersBundleTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Environment environment;

    private TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration;
    private TraceHeadersBundle<TraceHeadersApplicationConfiguration> traceHeadersBundle;

    @Before
    public void setUp() {
        setUpMocks();
        traceHeadersApplicationConfiguration = setUpTraceHeadersApplicationConfiguration(HEADER_METRIC_PREFIX,
                HEADERS_TO_TRACE_JSON);
        traceHeadersBundle = setUpTraceHeadersBundle(metricRegistry);
    }

    @Test
    public void headerMetricFeatureIsRegisteredProperly() throws Exception {
        traceHeadersBundle.run(traceHeadersApplicationConfiguration, environment);
        captureHeaderMetricFeatureRegistration();
    }

    @Test(expected = IllegalStateException.class)
    public void traceHeadersBundleConfigurationIsNull() throws Exception {
        TraceHeadersBundle<TraceHeadersApplicationConfiguration> bundleWithWrongConfig = new TraceHeadersBundle<TraceHeadersApplicationConfiguration>(
                metricRegistry) {

            @Override
            protected TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration(
                    TraceHeadersApplicationConfiguration configuration) {
                return null;
            }
        };
        bundleWithWrongConfig.run(traceHeadersApplicationConfiguration, environment);
    }

    private void setUpMocks() {
        metricRegistry = mock(MetricRegistry.class);
        environment = mock(Environment.class);
        when(environment.jersey()).thenReturn(mock(JerseyEnvironment.class));
    }

    private void captureHeaderMetricFeatureRegistration() {
        ArgumentCaptor<HeaderMetricFeature> featureCaptor = ArgumentCaptor.forClass(HeaderMetricFeature.class);
        verify(environment.jersey()).register(featureCaptor.capture());
        assertThat(featureCaptor.getAllValues()).isNotEmpty();
    }

}