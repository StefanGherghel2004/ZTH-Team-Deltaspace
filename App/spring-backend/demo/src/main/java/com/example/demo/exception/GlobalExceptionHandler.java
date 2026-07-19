package com.example.demo.exception;

import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.notfound.CommunityNotFoundException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.exception.notfound.UserNotFoundException;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage(), request, null);
    }

    @ExceptionHandler({
            CommentNotFoundException.class,
            PostNotFoundException.class,
            UserNotFoundException.class,
            CommunityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, null);
    }

    @ExceptionHandler({
            UserTooYoungException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONTENT_TOO_LARGE, "Max file size exceeded.", request, null);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorage(FileStorageException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "Validation failed"),
                        (first, second) -> first
                ));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataIntegrityViolationException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = Map.of("details", ex.getMostSpecificCause().getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Database error", request, errorDetails);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request, Map<String, String> errors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .time(LocalDateTime.now())
                .url(request.getRequestURI())
                .errors(errors)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @Data
    @Builder
    public static class ErrorResponse {
        private String message;
        private int status;
        private LocalDateTime time;
        private String url;
        private Map<String, String> errors; // detailed explanation
    }
}