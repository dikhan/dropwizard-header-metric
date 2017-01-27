package com.github.dikhan.dropwizard.headermetric.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>An annotation for marking a method of an annotated object as TraceConfiguredHeaders.</p>
 * Given a method like this:
 * <pre>
 *     <code>
 *     {@literal @}TraceConfiguredHeaders(name = "fancyName")
 *     public String fancyName(String name) {
 *         return "Sir Captain " + name;
 *     }
 *     </code>
 * </pre>
 * <p></p>
 * A counter for the defining class with the name {@code fancyName} will be created for each of the
 * headers configured in the yml file. And each time the method {@code #fancyName(String)} with such annotation in
 * place is invoked, the method's execution will be counted should any of the headers in the request match the
 * configured headers on start up.
 *
 * For instance, let's assume the above example is end actual GET end-point also annotated with @TraceConfiguredHeaders
 * annotation. Additionally, on start-up we have configured the following header (x-custom-header: x-custom-header-value)
 * to be tracked.
 *
 * If we receive a request to the given end point containing the aforementioned header, the counter metric associated with
 * that endpoint/header/value will be increased and reported using DropWizard's configured reporters.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TraceConfiguredHeaders {

    /**
     * @return The name of the end point where headers need to be tracked.
     */
    String name() default "";

}
