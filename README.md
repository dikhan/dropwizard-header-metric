# DropWizard Header Metrics [![Build Status][travis-image]][travis-url]

![][dropwizard-header-metric-logo]

Would you like to track down certain headers from the consumers of your service or even know who is calling your service 
and be able to report the usage to many different external services via [DropWizard supported reporters](http://metrics.dropwizard.io/3.1.0/manual/core/#reporters) 
such as console, csv, slf4j, graphite, ganglia, etc? If the answer is YES, then you are in the right place :)

This bundle lets you create metrics (based on expected headers) and send it to the preferable reporter if the request 
header matches any of the headers configured to be measured. Internally, for every match, the Counter metric associated 
with the header in question will be increased and depending upon the reporters frequency the metric will eventually get 
pushed to the configured reporting services.

Take a look at the '**How to use it**' section to check out how easy it is to integrate this module in your application 
and start tracking all the headers you wish!

[![License][license-image]][license-url]  |
[![version][maven-version]][maven-url]    |
[![Build Status][travis-image]][travis-url]


## How to use it

- Include the following dependency in the the Maven pom file (check out the versions available in Maven Central)

```
<dependency>
    <groupId>com.github.dikhan</groupId>
    <artifactId>dropwizard-header-metric</artifactId>
    <version>${dropwizard-header-metric.version}</version>
</dependency>
```

All versions available in Maven Central can be seen at [Maven Repo1](https://repo1.maven.org/maven2/com/github/dikhan/dropwizard-header-metric/)

- Add the @TraceConfiguredHeaders annotation (providing a value to the property 'name') to the end points you would like to trace the expected headers:

```
    @GET
    @TraceConfiguredHeaders(name="sayHelloWorld")
    public String sayHelloWorld() {
        return "Hello World";
    }
```

The annotation supports the configuration of a name. This value will be used to register the metrics and be able to
identify what exact endpoint which received the expected headers.

- Add the following to your DropWizard Configuration class:

```
public class YourApplicationConfiguration extends Configuration {

    @JsonProperty("traceHeaders")
    private TraceHeadersBundleConfiguration traceHeadersBundleConfiguration;

    public TraceHeadersBundleConfiguration getTraceHeadersBundleConfiguration() {
        return traceHeadersBundleConfiguration;
    }

    public void setTraceHeadersBundleConfiguration(TraceHeadersBundleConfiguration traceHeadersBundleConfiguration) {
        this.traceHeadersBundleConfiguration = traceHeadersBundleConfiguration;
    }
}

```

- Update DropWizard's application yml configuration file:

Both properties (headersToTraceJson and metricPrefix) are optional and can be left empty, though the empty String "" needs 
to be set.
The headersToTraceJson property value has to be properly formatted as Json object and the double quotes need to be escaped. 
The following structures are supported:
0. Empty Json object: {} No headers will be tracked in this case
1. Single key value: {"y-custom-header": "y-custom-header-value1"}
2. Key with multiple values: {"x-custom-header": ["x-custom-header-value1", "x-custom-header-value2"]}

```
traceHeaders:
  headersToTraceJson: "{\"x-custom-header\": [\"x-custom-header-value1\", \"x-custom-header-value2\"], \"y-custom-header\": \"y-custom-header-value1\"}"    
  metricPrefix: HeaderMetricPrefix
```

- In your Application class:

```
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
```

## Examples

The library contains a sample application already set up to work with the bundle. The application yml file is configured to trace certain headers and the HelloWorldResource @Path("/hello-world") @GET sayHelloWorld() endpoint is annotated with the custom annotation thus anytime the endpoint is called with some of the headers configured we should see the specific header counter incresed.

For info purposes, the applicaiton will log on start up the headers that are registered per end point. The following shows a sneak peak of the print out:
```
INFO  [2017-01-28 13:03:47,950] com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature: New Header Metric registered -> HeaderMetric-sayHelloWorld-x-custom-header-x-custom-header-value1
INFO  [2017-01-28 13:03:47,950] com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature: New Header Metric registered -> HeaderMetric-sayHelloWorld-x-custom-header-x-custom-header-value2
INFO  [2017-01-28 13:03:47,950] com.github.dikhan.dropwizard.headermetric.features.HeaderMetricFeature: New Header Metric registered -> HeaderMetric-sayHelloWorld-y-custom-header-y-custom-header-value1
```
In the avobe you can tell that we are using a prefix (HeaderMetric) as speciied in the yml configuration, followed by the endpoint with the annotaiton and lastly the header name and the value to be traced.

The sample application is also configured to use the 'console' reporter, so we are able to see what metrics are reported from time to time depending upon the frequency value. Below is one of the print outs in the console:

```
-- Counters --------------------------------------------------------------------
HeaderMetric-sayHelloWorld-x-custom-header-x-custom-header-value1
             count = 0
HeaderMetric-sayHelloWorld-x-custom-header-x-custom-header-value2
             count = 0
HeaderMetric-sayHelloWorld-y-custom-header-y-custom-header-value1
             count = 0
io.dropwizard.jetty.MutableServletContextHandler.active-dispatches
             count = 0
io.dropwizard.jetty.MutableServletContextHandler.active-requests
             count = 0
io.dropwizard.jetty.MutableServletContextHandler.active-suspended
```
If we were to perform a GET request to the end point annotated with @TraceConfiguredHeaders(name="sayHelloWorld") passing in for instance the header x-custom-header with value x-custom-header-value1. Then we should expect the counter of the given endpoint/header to be incresed by one.

```
GET /hello-world HTTP/1.1
HOST: localhost:<SERVER_PORT>
x-custom-header: X-CUSTOM-HEADER-VALUE1
```
And the logs should show:
```
-- Counters --------------------------------------------------------------------
HeaderMetric-sayHelloWorld-x-custom-header-x-custom-header-value1
             count = 1
```

## Contributing

- Fork it!
- Create your feature branch: git checkout -b my-new-feature
- Commit your changes: git commit -am 'Add some feature'
- Push to the branch: git push origin my-new-feature
- Submit a pull request :D

## Authors

Daniel I. Khan Ramiro - Cisco Systems

See also the list of [contributors](https://github.com/dikhan/dropwizard-header-metric/graphs/contributors) who 
participated in this project.


## Acknowledgements:

- DropWizard: https://github.com/dropwizard


[dropwizard-header-metric-logo]: https://github.com/dikhan/dropwizard-header-metric/blob/master/docs/images/dropwizard-header-metric.png

[license-url]: https://github.com/dikhan/dropwizard-header-metric/blob/master/LICENSE
[license-image]: https://img.shields.io/badge/license-MIT-blue.svg?style=flat

[travis-url]: https://travis-ci.org/dikhan/dropwizard-header-metric
[travis-image]: https://travis-ci.org/dikhan/dropwizard-header-metric.svg?branch=master

[maven-url]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22dropwizard-header-metric%22
[maven-version]: https://img.shields.io/maven-central/v/com.github.dikhan/dropwizard-header-metric.svg?style=flat

