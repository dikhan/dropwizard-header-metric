package com.github.dikhan.dropwizard.headermetric;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.dikhan.dropwizard.DropWizardApplication;
import com.github.dikhan.dropwizard.DropWizardApplicationConfiguration;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class IntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");

    @ClassRule
    public static final DropwizardAppRule<DropWizardApplicationConfiguration> RULE = new DropwizardAppRule<>(
            DropWizardApplication.class, CONFIG_PATH);

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
    public void testHelloWorld() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world").request()
                .get();
        assertThat(response.getEntity()).isEqualTo("Hello World");
    }

    @Test
    public void testHelloTo() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/hello-world/dani")
                .request().get();
        assertThat(response.getEntity()).isEqualTo("Hello dani");
    }

}
