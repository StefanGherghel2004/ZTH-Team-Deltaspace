package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    //private static final String URL = "http://localhost:5157/filters";
    private static final String URL =  "http://172.31.7.33:5157/filters"; // toggle this before ./build.ps1

    private final RestClient restClient = RestClient.create();

    @GetMapping("filters")
    public ResponseEntity<String> getFilters() {

        try {

            return restClient.get()
                    .uri(URL)
                    .retrieve()
                    .toEntity(String.class);

        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Error connecting to filter service" + e.getMessage());
        }
    }

    @GetMapping(produces = "text/plain;charset=UTF-8")
    public String getHomePage() {
        return """
    ==============================================
     W E L C O M E   T O   D E L T A   S P A C E
    ==============================================
    """;
    }

}
