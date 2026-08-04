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

    //private static final String URL = "http://localhost:5157/api/filter";
    private static final String URL =  "http://172.31.42.212:5157/api/filter"; // toggle this before ./build.ps1 for EC2

    public byte[] edit(MultipartFile file, Integer filterId) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        if (filterId == null || filterId < 1 || filterId > FILTERS.size()) {
            throw new IllegalArgumentException("Invalid filter ID. Must be between 1 and " + FILTERS.size());
        }

        String filterName = FILTERS.get(filterId - 1);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("imageFile", fileResource);
        body.add("filter", filterName);

        return restClient.post()
                .uri(URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(byte[].class);

    }
}
