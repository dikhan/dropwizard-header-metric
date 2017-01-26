package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.HEADER_METRIC_PREFIX;
import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import com.codahale.metrics.Counter;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.dikhan.dropwizard.TraceHeadersApplication;
import com.github.dikhan.dropwizard.TraceHeadersApplicationConfiguration;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");
    private static final String X_HEADER = "x-custom-header";
    private static final String X_HEADER_VALUE = "x-custom-header-value-1";
    private static final String X_HEADER_VALUE_2 = "x-custom-header-value-2";
    private static final String Y_HEADER = "y-custom-header";
    private static final String Y_HEADER_VALUE = "y-custom-header-value-1";

    @ClassRule
    public static final DropwizardAppRule<TraceHeadersApplicationConfiguration> RULE = new DropwizardAppRule<>(
            TraceHeadersApplication.class, CONFIG_PATH);

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
        resetMetrics();
    }

    private void resetMetrics() {
        RULE.getEnvironment().metrics().getCounters().entrySet().forEach(entry -> {
            Counter c = entry.getValue();
            if (c.getCount() > 0) {
                c.dec(c.getCount());
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void requestWithNoHeadersCallsTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE, 0);
    }

    @Test
    public void requestContainingMeasuredHeaderCallsTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .header(X_HEADER, X_HEADER_VALUE)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE, 1);
    }

    @Test
    public void requestContainingMeasuredHeadersWithMultipleValuesCallsTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .header(X_HEADER, X_HEADER_VALUE)
                .header(X_HEADER, X_HEADER_VALUE_2)
                .header(Y_HEADER, Y_HEADER_VALUE)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE, 1);
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE_2, 1);
        checkMeasuredHeaderCounter(Y_HEADER, Y_HEADER_VALUE, 1);
    }

    @Test
    public void requestWithNoMeasuredHeaderCallsTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .header("NonTrackedHeader", "NonTrackedHeaderValue")
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkNonMeasuredHeaderCounter("NonTrackedHeader", "NonTrackedHeaderValue");
    }

    @Test
    public void callNonTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE, 0);
    }

    @Test
    public void requestWithNonMeasuredHeaderCallsNonTraceConfiguredHeadersAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .header("NonTrackedHeader", "NonTrackedHeaderValue")
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkNonMeasuredHeaderCounter("NonTrackedHeader", "NonTrackedHeaderValue");
    }

    @Test
    public void requestWithMeasuredHeaderCallsNonHeaderMetricAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .header(X_HEADER, X_HEADER_VALUE)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // Even though the request contained tracked headers, since the end point called is not annotated with
        // the TraceConfiguredHeaders annotation, the counter will not get increased
        checkMeasuredHeaderCounter(X_HEADER, X_HEADER_VALUE, 0);
    }

    private void checkMeasuredHeaderCounter(String header, String headerValue, int count) {
        String expectedMetric = getExpectedMetric(header, headerValue);
        assertThat(RULE.getEnvironment().metrics().getCounters().get(expectedMetric).getCount()).isEqualTo(count);
    }

    private void checkNonMeasuredHeaderCounter(String header, String headerValue) {
        String expectedMetric = getExpectedMetric(header, headerValue);
        assertThat(RULE.getEnvironment().metrics().getCounters().get(expectedMetric)).isNull();
    }

    private String getExpectedMetric(String header, String headerValue) {
        return String.format("%s-%s-%s", HEADER_METRIC_PREFIX, header, headerValue);
    }

}
