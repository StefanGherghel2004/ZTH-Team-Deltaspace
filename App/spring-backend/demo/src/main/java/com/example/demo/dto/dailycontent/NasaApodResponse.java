package com.example.demo.dto.dailycontent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data Transfer Object representing the response from NASA's Astronomy Picture of the Day (APOD) API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NasaApodResponse(
        String title,
        String explanation,
        String url,
        String media_type,
        String thumbnail_url
) {}