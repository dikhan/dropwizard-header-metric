package com.github.dikhan.dropwizard.headermetric.filters;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;

public class TraceConfiguredHeadersFilterTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private ContainerRequestContext requestContext;

    private static final String REQUEST_HEADER_1 = "request_header_1";
    private static final String REQUEST_HEADER_1_VALUE = "request_header_1_value";
    private Counter counterHeader1 = new Counter();
    private static final String REQUEST_HEADER_2 = "request_header_2";
    private static final String REQUEST_HEADER_2_VALUE = "request_header_2_value";
    private Counter counterHeader2 = new Counter();

    @Before
    public void setUp() {
        metricRegistry = mock(MetricRegistry.class);
        requestContext = mock(ContainerRequestContext.class);
        initMetricRegistry();
        initRequestHeaders();
    }

    @Test
    public void testFilter() throws IOException {
        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);

        callFilter(headersAndValuesToLookUp, metricRegistry);

        ArgumentCaptor<String> metricCaptor = captureHeaderMetricCalls(1);
        verifyThatGivenHeaderHasBeenTracked(metricCaptor, REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE, counterHeader1);
    }

    @Test
    public void testFilterWithMultipleHeadersToTrack() throws IOException {
        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        headersAndValuesToLookUp.add(REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        headersAndValuesToLookUp.add(REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);

        callFilter(headersAndValuesToLookUp, metricRegistry);

        ArgumentCaptor<String> metricCaptor = captureHeaderMetricCalls(2);
        verifyThatGivenHeaderHasBeenTracked(metricCaptor, REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE, counterHeader1);
        verifyThatGivenHeaderHasBeenTracked(metricCaptor, REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE, counterHeader2);
    }

    @Test
    public void testFilterWithNonTraceableHeaders() throws IOException {
        // The two headers coming from the request are not measured and therefore metric registry should not be called
        MultivaluedMap<String, String> headersAndValuesToLookUp = new MultivaluedHashMap<>();
        callFilter(headersAndValuesToLookUp, metricRegistry);
        verifyZeroInteractions(metricRegistry);
    }

    private void callFilter(MultivaluedMap<String, String> headersAndValuesToLookUp, MetricRegistry metricRegistry)
            throws IOException {
        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = setUpTraceHeadersBundleConfigHelper(headersAndValuesToLookUp, metricRegistry);
        HeaderMetricFilter headerMetricFilter = new HeaderMetricFilter(traceHeadersBundleConfigHelper, headersAndValuesToLookUp, metricRegistry);
        headerMetricFilter.filter(requestContext);
    }

    private ArgumentCaptor<String> captureHeaderMetricCalls(int numExpectedMetricsCalls) {
        ArgumentCaptor<String> metricCaptor = ArgumentCaptor.forClass(String.class);
        verify(metricRegistry, times(numExpectedMetricsCalls)).counter(metricCaptor.capture());
        return metricCaptor;
    }

    private void verifyThatGivenHeaderHasBeenTracked(ArgumentCaptor<String> metricCaptor, String header,
            String headerValue, Counter headerCounter) {
        assertThat(metricCaptor.getAllValues()).contains(HEADER_METRIC_PREFIX + "-" + header + "-" + headerValue);
        assertThat(headerCounter.getCount()).isEqualTo(1);
    }

    private void initMetricRegistry() {
        when(metricRegistry.counter(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_1 + "-" + REQUEST_HEADER_1_VALUE)).thenReturn(
                counterHeader1);
        when(metricRegistry.counter(HEADER_METRIC_PREFIX + "-" + REQUEST_HEADER_2 + "-" + REQUEST_HEADER_2_VALUE)).thenReturn(
                counterHeader2);
    }

    private void initRequestHeaders() {
        MultivaluedMap<String, String> requestHeaders = new MultivaluedHashMap<>();
        requestHeaders.add(REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        requestHeaders.add(REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);
        when(requestContext.getHeaders()).thenReturn(requestHeaders);
    }

}