package com.github.dikhan.dropwizard.headermetric.utils;

import java.lang.annotation.Annotation;

import com.github.dikhan.dropwizard.headermetric.annotations.TraceConfiguredHeaders;

public class TraceConfiguredHeadersImpl implements TraceConfiguredHeaders {

    private final String name;

    public TraceConfiguredHeadersImpl(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
