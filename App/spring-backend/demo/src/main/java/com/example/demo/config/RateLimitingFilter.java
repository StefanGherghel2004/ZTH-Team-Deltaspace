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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

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

            requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
            int currentCount = requestCounts.get(clientIp).incrementAndGet();

            if (currentCount > MAX_REQUESTS_PER_MINUTE) {
                exceptionResolver.resolveException(
                        request,
                        response,
                        null,
                        new RateLimitExceededException("Too many requests. Please try again later.")
                );
                return;

            }
        }

        filterChain.doFilter(request, response);
    }
}