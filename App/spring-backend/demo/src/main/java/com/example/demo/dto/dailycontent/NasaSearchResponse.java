package com.example.demo.dto.dailycontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Data Transfer Object representing the response structure from NASA's Image and Video Library Search API.
 */
public record NasaSearchResponse(
        Collection collection
) {
    public record Collection(
            List<Item> items
    ) {}

    public record Item(
            List<Data> data,
            List<Link> links
    ) {}

    public record Data(
            String title,
            String description,
            @JsonProperty("nasa_id") String nasaId,
            @JsonProperty("date_created") String dateCreated
    ) {}

    public record Link(
            String href,
            String rel,
            String render
    ) {}
}