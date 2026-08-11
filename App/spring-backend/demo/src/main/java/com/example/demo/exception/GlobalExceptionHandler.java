package com.example.demo.exception;

import com.example.demo.exception.notfound.NotFoundException;
import com.example.demo.response.ApiError;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.ErrorDetail;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.exception.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //400 BAD REQUEST
    @ExceptionHandler({
            UserTooYoungException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("request", e.getMessage())
        );
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage(), request, details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorDetail> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation failed"
                ))
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Data provided is not valid", request, details);
    }

    //401 UNAUTHORIZED
    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class,
            ExpiredJwtException.class,
            JwtException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(Exception e, HttpServletRequest request) {
        String message = (e instanceof ExpiredJwtException) ? "This token has expired." : e.getMessage();
        List<ErrorDetail> details = List.of(
                new ErrorDetail("authentication", message)
        );
        return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, request, details);
    }

    //403 FORBIDDEN
    @ExceptionHandler({
            AccessDeniedException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleForbidden(Exception e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("authorization", e.getMessage())
        );
        return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage(), request, details);
    }

    //404 NOT FOUND
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("resource", e.getMessage())
        );
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), request, details);
    }

    //409 CONFLICT
    @ExceptionHandler(DuplicateUserInformationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(Exception e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("conflict", e.getMessage())
        );
        return buildResponse(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), request, details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("database", "Database constraint violation")
        );
        return buildResponse(HttpStatus.CONFLICT, "DATA_CONFLICT", "Database resource conflict or constraint violation.", request, details);
    }

    //413 PAYLOAD TOO LARGE
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("file", "File size exceeds maximum allowed upload limit.")
        );
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Max file size exceeded.", request, details);
    }

    //422 UNPROCESSABLE ENTITY
    @ExceptionHandler({
            IdenticalPasswordException.class,
            BusinessLogicException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBusinessLogic(Exception e, HttpServletRequest request) {
        String field = (e instanceof IdenticalPasswordException) ? "newPassword" : "businessRule";
        List<ErrorDetail> details = List.of(
                new ErrorDetail(field, e.getMessage())
        );
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", e.getMessage(), request, details);
    }

    //429 TOO MANY REQUESTS
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitExceededException e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("rateLimit", e.getMessage())
        );
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", e.getMessage(), request, details);
    }

    //500 INTERNAL SERVER ERROR
    @ExceptionHandler({
            FileStorageException.class,
            Exception.class
    })
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(
                new ErrorDetail("server", "An internal error occurred. Please try again later.")
        );
        e.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", request, details);
    }

    private ResponseEntity<ApiResponse<Void>> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<ErrorDetail> details) {

        ApiError apiError = new ApiError(code, message, details != null ? details : List.of());
        ApiResponse<Void> response = ApiResponse.error(apiError, request.getRequestURI());

        return ResponseEntity.status(status).body(response);
    }
}