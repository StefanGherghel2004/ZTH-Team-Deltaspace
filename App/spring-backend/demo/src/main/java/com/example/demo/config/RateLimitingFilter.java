package com.example.demo.config; // sau pachetul tău curent

import com.example.demo.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long TIME_WINDOW_MS = Duration.ofMinutes(1).toMillis();

    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
    private final HandlerExceptionResolver exceptionResolver;

    public RateLimitingFilter(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/auth/login")) {
            String clientIp = request.getRemoteAddr();
            long currentTime = System.currentTimeMillis();


            List<Long> timestamps = requestTimestamps.computeIfAbsent(clientIp, k -> Collections.synchronizedList(new LinkedList<>()));

            synchronized (timestamps) {

                // removing timestamps outside of window
                timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW_MS);

                if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
                    exceptionResolver.resolveException(
                            request,
                            response,
                            null,
                            new RateLimitExceededException("Too many requests. Please try again later.")
                    );
                    return;
                }

                // add current request timestamp
                timestamps.add(currentTime);
            }
        }

        filterChain.doFilter(request, response);

    }

}