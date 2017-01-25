package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.Constants.*;
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

    private TraceHeadersBundle traceHeadersBundle;

    @Before
    public void setUp() {
        metricRegistry = mock(MetricRegistry.class);
        environment = mock(Environment.class);
        when(environment.jersey()).thenReturn(mock(JerseyEnvironment.class));

        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        headersAndValuesToLookUp.add(REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);
        traceHeadersBundle = new TraceHeadersBundle(headersAndValuesToLookUp, metricRegistry);
    }

    @Test
    public void headerMetricsAreRegisteredCorrectly() {
        traceHeadersBundle.run(environment);
        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE);
    }

    @Test
    public void handleHeadersAndValuesToLookUpInUpperCase() {

        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1.toUpperCase(), REQUEST_HEADER_1_VALUE.toUpperCase());
        headersAndValuesToLookUp.add(REQUEST_HEADER_2.toUpperCase(), REQUEST_HEADER_2_VALUE.toUpperCase());
        traceHeadersBundle = new TraceHeadersBundle(headersAndValuesToLookUp, metricRegistry);

        traceHeadersBundle.run(environment);

        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE);
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE);
    }

    private ArgumentCaptor<String> captureHeaderMetricRegistrations(int numExpectedMetricsCalls) {
        ArgumentCaptor<String> metricCaptor = ArgumentCaptor.forClass(String.class);
        verify(metricRegistry, times(numExpectedMetricsCalls)).register(metricCaptor.capture(), any(Counter.class));
        return metricCaptor;
    }

}