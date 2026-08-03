package com.example.demo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Value;

import java.time.Instant;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse<T> {

    private String message;

    private boolean success;
    private T data;
    private ApiError error;
    private Instant timestamp;
    private String path;
    private Integer total;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(T data,Integer total){
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(total);
        return response;
    }

    // used for deletions
    public static ApiResponse<Void> successMessage(String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static ApiResponse<Void> error(ApiError apiError, String path) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(apiError);
        response.setTimestamp(Instant.now());
        response.setPath(path);
        return response;

    }
}
