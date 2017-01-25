package com.github.dikhan.dropwizard.headermetric.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.dikhan.dropwizard.headermetric.annotations.TraceConfiguredHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldResource.class);

    @GET
    @TraceConfiguredHeaders
    public String sayHelloWorld() {
        return "Hello World";
    }

    @GET
    @Path("/{name}")
    public String sayHelloTo(@PathParam("name") String name) {
        return "Hello " + name;
    }

}
