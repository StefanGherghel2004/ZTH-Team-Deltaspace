package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageEditService {

    private final RestClient restClient = RestClient.create();

    private static final List<String> FILTERS = List.of("Grayscale","Invert","Sepia","Neon");

    //private static final String URL = "http://localhost:5157/api/filter";
    private static final String URL =  "http://172.31.7.33:5157/api/filter"; // toggle this before ./build.ps1 for EC2

    public Integer getValidFilterId(Integer filterId) {
        if (filterId == null || filterId < 1 || filterId > FILTERS.size()) {
            return null;
        }
        return filterId;
    }

    public void edit(String downloadUrl, String uploadUrl, Integer filterId) throws IOException {

        String filterName = FILTERS.get(filterId - 1);

        Map<String, String> payload = Map.of(
                "downloadUrl", downloadUrl,
                "uploadUrl", uploadUrl,
                "filter", filterName
        );

        restClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

    }
}
