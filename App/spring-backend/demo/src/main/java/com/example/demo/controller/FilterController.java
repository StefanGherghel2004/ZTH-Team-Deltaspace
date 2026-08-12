package com.example.demo.controller;

import com.example.demo.logger.Logger;
import com.example.demo.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filters")
public class FilterController {

    @Value("${filter.controller.url}")
    private String URL;

    private final RestClient restClient = RestClient.create();

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFilters() {
// LOG TRACE START AND END
        try {
            ApiResponse<?> responseFromFilterService = restClient.get()
                    .uri(URL)
                    .retrieve()
                    .body(ApiResponse.class);

            return ResponseEntity.ok(responseFromFilterService);

        } catch (RestClientException e) {
            // LOG EXCEPTION ROOT CAUSE OR SOMETHING TO HELP WITH DEBUG
            // THINK ABOUT WRITING A MESSAGE IN THE ERROR RESPONSE
            // use global exception handler
            Logger.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(null, "/filters"));
        }
    }
}
