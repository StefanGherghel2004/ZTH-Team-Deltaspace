package com.example.demo.service;


import com.example.demo.logger.Logger;
import com.example.demo.model.Filter;
import com.example.demo.repository.FilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


import java.util.Map;

/**
 * Service responsible for validating image filters and communicating with an external
 * microservice to apply specific visual filters on uploaded images.
 */
@Service
@RequiredArgsConstructor
public class ImageEditService {

    private final RestClient restClient = RestClient.create();
    private final FilterRepository filterRepository;

    @Value("${filter.service.url}")
    private String url;

    /**
     * Validates whether a given filter ID exists in the database.
     *
     * @param filterId the identifier of the filter to validate
     * @return the same filter ID if it exists, or null if the ID is null or not found
     */
    public Integer getValidFilterId(Integer filterId) {
        if (filterId == null) {
            return null;
        }

        if (filterRepository.existsById(filterId.longValue())) {
            return filterId;
        }

        return null;
    }

    /**
     * Sends a request to the external image processing API to apply a filter on an image
     * located at a pre-signed download URL and upload the processed result to a pre-signed upload URL.
     *
     * @param downloadUrl the pre-signed S3 URL to download the original image
     * @param uploadUrl   the pre-signed S3 URL to upload the edited image
     * @param filterId    the identifier of the filter to be applied
     */
    @Transactional
    public void edit(String downloadUrl, String uploadUrl, Integer filterId) {

        Logger.info("Attempting to edit image with filterId: {}", filterId);

        Filter filter = filterRepository.findById(filterId.longValue())
                .orElseThrow(() -> {
                    Logger.warning("Image edit failed: Filter with id = {} not found", filterId);
                    return new IllegalArgumentException("Filter with id = " + filterId + " not found");
                });

        // don't send the request to edit service
        if ("none".equalsIgnoreCase(filter.getName())) {
            return;
        }

        Map<String, String> payload = Map.of(
                "downloadUrl", downloadUrl,
                "uploadUrl", uploadUrl,
                "filter", filter.getName()
        );

        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            filterRepository.incrementUsageCount(Long.valueOf(filterId));
            Logger.info("Image edit request successfully sent for filter: {}", filter.getName());
        } catch (RestClientException e) {
            Logger.warning("Failed to communicate with image editing service for filterId {}: {}", filterId, e.getMessage(), e);
            throw new RuntimeException("Image editing service communication failed", e);
        }
    }
}

