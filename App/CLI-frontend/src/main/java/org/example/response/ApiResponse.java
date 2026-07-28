package org.example.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;



@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    private String message;

    private boolean success;
    private T data;
    private ApiError error;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
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
        response.setPath(path);
        return response;

    }
}
