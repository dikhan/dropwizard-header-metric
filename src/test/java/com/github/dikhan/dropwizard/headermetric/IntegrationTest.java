package com.github.dikhan.dropwizard.headermetric;

import static com.github.dikhan.dropwizard.headermetric.utils.TestHelper.HEADER_METRIC_PREFIX;
import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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
    private static final String HEADER = "x-custom-header";
    private static final String HEADER_VALUE = "x-custom-header-value";

    @ClassRule
    public static final DropwizardAppRule<TraceHeadersApplicationConfiguration> RULE = new DropwizardAppRule<>(
            TraceHeadersApplication.class, CONFIG_PATH);

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void callHeaderMetricAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(HEADER, HEADER_VALUE, 0);
    }

    @Test
    public void callHeaderMetricAnnotatedEndPointWithMeasuredHeader() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .header(HEADER, HEADER_VALUE)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(HEADER, HEADER_VALUE, 1);
    }

    @Test
    public void callHeaderMetricAnnotatedEndPointWithNonMeasuredHeader() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world")
                .request()
                .header("NonTrackedHeader", "NonTrackedHeaderValue")
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkNonMeasuredHeaderCounter("NonTrackedHeader", "NonTrackedHeaderValue");
    }

    @Test
    public void callNonHeaderMetricAnnotatedEndPoint() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkMeasuredHeaderCounter(HEADER, HEADER_VALUE, 0);
    }

    @Test
    public void callNonHeaderMetricAnnotatedEndPointWithNonMeasuredHeader() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .header("NonTrackedHeader", "NonTrackedHeaderValue")
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        checkNonMeasuredHeaderCounter("NonTrackedHeader", "NonTrackedHeaderValue");
    }

    @Test
    public void callNonHeaderMetricAnnotatedEndPointWithMeasuredHeader() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request()
                .header(HEADER, HEADER_VALUE)
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // Even though the request contained tracked headers, since the end point called is not annotated with
        // the TraceConfiguredHeaders annotation, the counter will not get increased
        checkMeasuredHeaderCounter(HEADER, HEADER_VALUE, 0);
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
