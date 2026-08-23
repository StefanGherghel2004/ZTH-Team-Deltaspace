package com.example.demo.dto.dailycontent;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing the response from NASA's Near-Earth Object Web Service (NeoWs) feed API.
 */
public record NasaNeoResponse(
        @JsonProperty("near_earth_objects") Map<String, List<Asteroid>> nearEarthObjects
) {
    public record Asteroid(
            String name,
            @JsonProperty("estimated_diameter") EstimatedDiameter estimatedDiameter,
            @JsonProperty("is_potentially_hazardous_asteroid") boolean isHazardous,
            List<CloseApproachData> closeApproachData
    ) {}

    public record EstimatedDiameter(
            @JsonProperty("kilometers") DiameterKm kilometers
    ) {}

    public record DiameterKm(
            @JsonProperty("estimated_diameter_min") double min,
            @JsonProperty("estimated_diameter_max") double max
    ) {}

    public record CloseApproachData(
            @JsonProperty("close_approach_date_full") String closeApproachDateFull
    ) {}
}
