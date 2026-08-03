package com.example.demo.exception;

import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.notfound.CommunityNotFoundException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.exception.notfound.UserNotFoundException;
import com.example.demo.response.ApiError;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.ErrorDetail;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "This token expired.", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage(), request, null);
    }

    @ExceptionHandler({
            CommentNotFoundException.class,
            PostNotFoundException.class,
            UserNotFoundException.class,
            CommunityNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), request, null);
    }

    @ExceptionHandler({
            UserTooYoungException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage(), request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONTENT_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Max file size exceeded.", request, null);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileStorage(FileStorageException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORAGE_ERROR", e.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorDetail> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation failed"
                ))
                .collect(Collectors.toList());

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Data provided is not valid", request, details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseError(DataIntegrityViolationException ex, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("database", ex.getMostSpecificCause().getMessage())
        );
        return buildResponse(HttpStatus.CONFLICT, "CONFLICT", "Database conflict error", request, details);
    }


    private ResponseEntity<ApiResponse<Void>> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<ErrorDetail> details) {


        ApiError apiError = new ApiError(code, message, details);

        ApiResponse<Void> response = ApiResponse.error(apiError, request.getRequestURI());

        return ResponseEntity.status(status).body(response);
    }
}