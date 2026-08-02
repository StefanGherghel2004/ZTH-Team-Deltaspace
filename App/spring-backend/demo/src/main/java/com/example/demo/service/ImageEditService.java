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

@Service
@RequiredArgsConstructor
public class ImageEditService {

    private final RestClient restClient = RestClient.create();

    private static final List<String> FILTERS = List.of("Grayscale","Invert","Sepia","Neon");

    private static final String URL = "http://localhost:5157/api/filter";

    public byte[] edit(MultipartFile file, String filter) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        boolean isValid = FILTERS.stream().anyMatch(f -> f.equalsIgnoreCase(filter));

        if (!isValid) {
            throw new IllegalArgumentException("This filter is not implemented. Available filters: " + FILTERS);
        }

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("imageFile", fileResource);
        body.add("filter", filter);

        return restClient.post()
                .uri(URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(byte[].class);

    }
}
