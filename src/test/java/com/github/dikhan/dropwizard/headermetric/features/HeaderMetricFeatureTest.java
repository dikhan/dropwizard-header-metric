package com.github.dikhan.dropwizard.headermetric.features;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.dikhan.dropwizard.headermetric.TraceHeadersBundleConfigHelper;
import com.github.dikhan.dropwizard.headermetric.resources.TestResource;
import com.github.dikhan.dropwizard.headermetric.utils.TestHelper;

public class HeaderMetricFeatureTest {

    @Mock
    private FeatureContext featureContext;

    @Mock
    private ResourceInfo resourceInfo;

    @Mock
    private MetricRegistry metricRegistry;

    private Method annotatedResourceEndPoint;
    private String resourceEndPointCanonicalName;

    private static final String REQUEST_HEADER_1 = "request_header_1";
    private static final String REQUEST_HEADER_1_VALUE = "request_header_1_value_1";
    private static final String REQUEST_HEADER_1_VALUE_2 = "request_header_1_value_2";
    private static final String REQUEST_HEADER_2 = "request_header_2";
    private static final String REQUEST_HEADER_2_VALUE = "request_header_2_value";

    private static final String HEADERS_TO_TRACE_JSON = String.format("{\"%s\": [\"%s\", \"%s\"], \"%s\": \"%s\"}",
            REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE, REQUEST_HEADER_1_VALUE_2, REQUEST_HEADER_2,
            REQUEST_HEADER_2_VALUE);

    @Before
    public void setUp() throws Exception {
        annotatedResourceEndPoint = TestResource.class.getDeclaredMethod("sayHelloWorld", null);
        resourceEndPointCanonicalName = endPointCanonicalName(TestResource.class, annotatedResourceEndPoint.getName());
        setUpMocks();
    }

    @Test
    public void headerMetricsAreRegisteredCorrectly() throws Exception {
        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = setUpTraceHeadersBundleConfigHelper(
                HEADERS_TO_TRACE_JSON, metricRegistry);
        HeaderMetricFeature headerMetricFeature = new HeaderMetricFeature(traceHeadersBundleConfigHelper,
                metricRegistry);

        headerMetricFeature.configure(resourceInfo, featureContext);

        captureHeaderMetricFilterRegistration();
        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(3);
        verifyHeaderCounterIsRegistered(metricCaptor, REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        verifyHeaderCounterIsRegistered(metricCaptor, REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE_2);
        verifyHeaderCounterIsRegistered(metricCaptor, REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);
    }

    @Test
    public void handleHeadersAndValuesToLookUpInUpperCase() throws Exception {
        String headersToTraceJson = String.format("{\"%s\": \"%s\", \"%s\": \"%s\"}", REQUEST_HEADER_1.toUpperCase(),
                REQUEST_HEADER_1_VALUE.toUpperCase(), REQUEST_HEADER_2.toUpperCase(),
                REQUEST_HEADER_2_VALUE.toUpperCase());

        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = setUpTraceHeadersBundleConfigHelper(
                headersToTraceJson, metricRegistry);
        HeaderMetricFeature headerMetricFeature = new HeaderMetricFeature(traceHeadersBundleConfigHelper,
                metricRegistry);

        headerMetricFeature.configure(resourceInfo, featureContext);

        captureHeaderMetricFilterRegistration();
        ArgumentCaptor<String> metricCaptor = captureHeaderMetricRegistrations(2);
        verifyHeaderCounterIsRegistered(metricCaptor, REQUEST_HEADER_1, REQUEST_HEADER_1_VALUE);
        verifyHeaderCounterIsRegistered(metricCaptor, REQUEST_HEADER_2, REQUEST_HEADER_2_VALUE);
    }

    @Test
    public void multivaluedMapFromHeadersToTraceJsonIsEmpty() throws Exception {
        String emptyHeadersToTraceJson = "";
        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = setUpTraceHeadersBundleConfigHelper(
                emptyHeadersToTraceJson, metricRegistry);
        HeaderMetricFeature headerMetricFeature = new HeaderMetricFeature(traceHeadersBundleConfigHelper,
                metricRegistry);

        headerMetricFeature.configure(resourceInfo, featureContext);

        captureHeaderMetricFilterRegistration();
        captureHeaderMetricRegistrations(0);
    }

    @Test
    public void resourceMethodDoesNotHaveTraceConfiguredHeadersAnnotation() throws Exception {
        TraceHeadersBundleConfigHelper traceHeadersBundleConfigHelper = setUpTraceHeadersBundleConfigHelper(
                HEADERS_TO_TRACE_JSON, metricRegistry);
        HeaderMetricFeature headerMetricFeature = new HeaderMetricFeature(traceHeadersBundleConfigHelper,
                metricRegistry);

        Method nonTraceConfiguredHeadersAnnotatedResourceEndPoint = TestResource.class.getDeclaredMethod("sayHelloTo",
                String.class);
        when(resourceInfo.getResourceMethod()).thenReturn(nonTraceConfiguredHeadersAnnotatedResourceEndPoint);

        headerMetricFeature.configure(resourceInfo, featureContext);
        metricFilterIsNotRegistered();
    }

    private void verifyHeaderCounterIsRegistered(ArgumentCaptor<String> metricCaptor, String header, String headerValue)
            throws NoSuchMethodException {
        assertThat(metricCaptor.getAllValues()).contains(
                TestHelper.getExpectedMetric(resourceEndPointCanonicalName, header, headerValue));
    }

    private void setUpMocks() throws NoSuchMethodException {
        resourceInfo = mock(ResourceInfo.class);
        featureContext = mock(FeatureContext.class);
        metricRegistry = mock(MetricRegistry.class);
        when(resourceInfo.getResourceMethod()).thenReturn(annotatedResourceEndPoint);
    }

    private void captureHeaderMetricFilterRegistration() {
        ArgumentCaptor<HeaderMetricFeature> featureCaptor = setUpFeatureCaptor();
        assertThat(featureCaptor.getAllValues()).isNotEmpty();
    }

    private void metricFilterIsNotRegistered() {
        verify(featureContext, never()).register(any());
    }

    private ArgumentCaptor<HeaderMetricFeature> setUpFeatureCaptor() {
        ArgumentCaptor<HeaderMetricFeature> featureCaptor = ArgumentCaptor.forClass(HeaderMetricFeature.class);
        verify(featureContext).register(featureCaptor.capture());
        return featureCaptor;
    }

    private ArgumentCaptor<String> captureHeaderMetricRegistrations(int numExpectedMetricsCalls) {
        ArgumentCaptor<String> metricCaptor = ArgumentCaptor.forClass(String.class);
        verify(metricRegistry, times(numExpectedMetricsCalls)).register(metricCaptor.capture(), any(Counter.class));
        return metricCaptor;
    }

}