# DropWizard Header Metrics [![Build Status][travis-image]][travis-url]

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

- Add the Maven dependency (available in Maven Central)

```
<dependency>
    <groupId>com.github.dikhan</groupId>
    <artifactId>dropwizard-header-metric</artifactId>
    <version>1.1</version>
</dependency>
```

- Add the @TraceConfiguredHeaders annotation to the end points you would like to trace the expected headers:

```
    @GET
    @TraceConfiguredHeaders(name="sayHelloWorld")
    public String sayHelloWorld() {
        return "Hello World";
    }
```

The annotation supports the configuration of a name. This value will be used to register the metrics and be able to
identify what exact endpoint got the expected headers.

- Add the following to your Configuration class:

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

- Add the following your configuration yml:

Both properties (headersToTraceJson and metricPrefix)are optional and can be left empty, though the empty String "" needs 
to be set.
The headersToTraceJson property value has to be properly formatter as Json object and the double quotes need to be escaped. 
The following structures are supported:
0. Empty Json object: No headers will be tracked in this case
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

Please note that the method getHeadersToMeasure() is an example and can be replaced with custom implementation. 
For instance, headersToMeasure map could be injected using a DI framework and passed in directly to the TraceHeadersBundle
when constructing the object.

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

[license-url]: https://github.com/dikhan/dropwizard-header-metric/blob/master/LICENSE
[license-image]: https://img.shields.io/badge/license-MIT-blue.svg?style=flat

[travis-url]: https://travis-ci.org/dikhan/dropwizard-header-metric
[travis-image]: https://travis-ci.org/dikhan/dropwizard-header-metric.svg?branch=master

[maven-url]: http://search.maven.org/#search%7Cga%7C1%7Ccom.github.dikhan
[maven-version]: https://img.shields.io/maven-central/v/com.github.dikhan/dropwizard-header-metric.svg?style=flat

