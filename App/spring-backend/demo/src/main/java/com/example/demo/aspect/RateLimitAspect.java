package com.example.demo.aspect;

import com.example.demo.exception.RateLimitExceededException;
import com.example.demo.annotation.RateLimit;
import com.example.demo.logger.Logger;
import com.example.demo.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * AOP component that intercepts methods
 * annotated with {@link RateLimit} to enforce traffic limiting.
 * It utilizes the Token Bucket algorithm (via Bucket4j) to manage rate limits
 * and stores the usage state of each client in an auto-evicting
 * Caffeine cache to prevent memory leaks.
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final long CACHE_EXPIRE_TIME_H = 1;
    private static final long CACHE_MAX_SIZE = 10_000;

    /**
     * An in-memory cache storing the token buckets for each client and method.
     * - {@code expireAfterAccess(1 hour)}: Automatically removes buckets if a client is inactive for an hour.
     * - {@code maximumSize(10_000)}: Caps the cache size to prevent OutOfMemory errors during high traffic
     */
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(CACHE_EXPIRE_TIME_H))
            .maximumSize(CACHE_MAX_SIZE)
            .build();

    private final HttpServletRequest request;
    private final UserService userService;

    public RateLimitAspect(HttpServletRequest request, UserService userService) {
        this.request = request;
        this.userService = userService;
    }

    /**
     * The core advice that wraps methods annotated with {@link RateLimit}.
     * It checks if the client has available tokens in their bucket before allowing the method to execute.
     *
     * @param joinPoint           the intercepted method execution point
     * @param rateLimitAnnotation the specific {@code @RateLimit} rules applied to the intercepted method
     * @return the result of the original method execution if the rate limit is not exceeded
     * @throws Throwable                  if the underlying method throws an exception during execution
     * @throws RateLimitExceededException if the client has exhausted their allowed requests
     */
    @Around("@annotation(rateLimitAnnotation)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimitAnnotation) throws Throwable {

        // extract the name of the method being accessed (e.g., "login")
        String methodName = joinPoint.getSignature().getName();

        String identifier = userService.currentUsernameOrNull();

        // if the user is not logged in the identifier is the IP address
        if (identifier == null || identifier.isBlank()) {
            identifier = request.getRemoteAddr();
        }

        String key = identifier + ":" + methodName;

        Bucket bucket = cache.get(key, k -> createNewBucket(rateLimitAnnotation));

        if (!bucket.tryConsume(1)) {
            Logger.warning("Too many request on " + key);
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }

        // allows the method to run
        return joinPoint.proceed();
    }

    /**
     * Method that creates a new Bucket4j {@link Bucket} based on the rules
     * defined in the {@link RateLimit} annotation.
     *
     * @param annotation the annotation containing the rate limit configuration (requests, duration, unit)
     * @return a newly configured Token Bucket
     */
    private Bucket createNewBucket(RateLimit annotation) {
        // convert the time unit
        Duration timeWindow = Duration.of(annotation.duration(), annotation.unit().toChronoUnit());

        Refill refill = Refill.greedy(annotation.requests(), timeWindow);
        Bandwidth limit = Bandwidth.classic(annotation.requests(), refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}