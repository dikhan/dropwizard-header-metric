package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.TraceHeadersApplicationConfiguration;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;

public class TraceConfiguredHeadersBundleTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Environment environment;

    private static final String REQUEST_HEADER_1 = "request_header_1";
    private static final String REQUEST_HEADER_1_VALUE = "request_header_1_value";
    private static final String REQUEST_HEADER_2 = "request_header_2";
    private static final String REQUEST_HEADER_2_VALUE = "request_header_2_value";

    private TraceHeadersApplicationConfiguration traceHeadersApplicationConfiguration;
    private TraceHeadersBundle<TraceHeadersApplicationConfiguration> traceHeadersBundle;

    @Before
    public void setUp() {
        setUpMocks();
        MultivaluedMap<String, String> headersAndValuesToLookUp = setUpHeadersAndValuesToLookUp();
        traceHeadersApplicationConfiguration = setUpTraceHeadersApplicationConfiguration(HEADER_METRIC_PREFIX);
        traceHeadersBundle = setUpTraceHeadersBundle(headersAndValuesToLookUp, metricRegistry);
    }

    @Test
    public void headerMetricsAreRegisteredCorrectly() throws Exception {
        traceHeadersBundle.run(traceHeadersApplicationConfiguration, environment);
        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE);
    }

    @Test
    public void handleHeadersAndValuesToLookUpInUpperCase() throws Exception {

        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1.toUpperCase(), REQUEST_HEADER_1_VALUE.toUpperCase());
        headersAndValuesToLookUp.add(REQUEST_HEADER_2.toUpperCase(), REQUEST_HEADER_2_VALUE.toUpperCase());
        traceHeadersBundle = setUpTraceHeadersBundle(headersAndValuesToLookUp, metricRegistry);

        traceHeadersBundle.run(traceHeadersApplicationConfiguration, environment);

        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE);
    }

    @Test
    public void headerMetricsAreRegisteredCorrectlyWithNoPrefix() throws Exception {
        traceHeadersApplicationConfiguration = setUpTraceHeadersApplicationConfiguration("");
        traceHeadersBundle.run(traceHeadersApplicationConfiguration, environment);
        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        assertThat(metricCaptor.getAllValues()).contains(REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE);
        assertThat(metricCaptor.getAllValues()).contains(REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE);
    }

    private void setUpMocks() {
        metricRegistry = mock(MetricRegistry.class);
        environment = mock(Environment.class);
        when(environment.jersey()).thenReturn(mock(JerseyEnvironment.class));
    }

    private MultivaluedMap<String, String> setUpHeadersAndValuesToLookUp() {
        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        headersAndValuesToLookUp.add(REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);
        return headersAndValuesToLookUp;
    }

    private ArgumentCaptor<String> captureHeaderMetricRegistrations(int numExpectedMetricsCalls) {
        ArgumentCaptor<String> metricCaptor = ArgumentCaptor.forClass(String.class);
        verify(metricRegistry, times(numExpectedMetricsCalls)).register(metricCaptor.capture(), any(Counter.class));
        return metricCaptor;
    }

}