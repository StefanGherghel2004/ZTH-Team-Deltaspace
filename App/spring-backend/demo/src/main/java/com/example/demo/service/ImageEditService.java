package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageEditService {

    private final RestClient restClient = RestClient.create();

    private static final List<String> FILTERS = List.of("Grayscale","Invert","Sepia","Neon");

    private static final String URL = "http://localhost:5157/api/filter";
    //private static final String URL =  "http://172.31.42.212:5157/api/filter"; // toggle this before ./build.ps1 for EC2

    public void edit(String downloadUrl, String uploadUrl, String filter) throws IOException {

        boolean isValid = FILTERS.stream().anyMatch(f -> f.equalsIgnoreCase(filter));

        if (!isValid) {
            throw new IllegalArgumentException("This filter is not implemented. Available filters: " + FILTERS);
        }

        Map<String, String> payload = Map.of(
                "downloadUrl", downloadUrl,
                "uploadUrl", uploadUrl,
                "filter", filter
        );


        restClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

    }
}
