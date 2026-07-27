package com.example.demo.response;

import lombok.Data;

import java.util.List;

@Data
public  class ApiError {
    private String code;
    private String message;
    private List<ErrorDetail> details;

    public ApiError(String code, String message, List<ErrorDetail> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}