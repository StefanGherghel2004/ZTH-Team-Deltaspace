package com.example.demo.controller;

import com.example.demo.dto.filter.FilterDto;
import com.example.demo.logger.Logger;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filters")
public class FilterController {

    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FilterDto>>> getFilters() {
        Logger.info("GET /filters request received");
        List<FilterDto> filters = filterService.getAllFilters();

        return ResponseEntity.ok(ApiResponse.success(filters));

    }
}
