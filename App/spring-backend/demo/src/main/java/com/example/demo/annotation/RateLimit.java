package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation used to enforce rate limiting on a specific endpoint, restricting
 * the number of requests a client can make within a given time window.
 * <b>Usage Example:</b>
 * <pre>{@code
 * @RateLimit(requests = 10, duration = 1, unit = TimeUnit.MINUTES)
 * @GetMapping("/api/resource")
 * public ResponseEntity<?> getResource() {
 *     return ResponseEntity.ok().build();
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    int requests() default 5;
    int duration() default 1;
    TimeUnit unit() default TimeUnit.MINUTES;
}