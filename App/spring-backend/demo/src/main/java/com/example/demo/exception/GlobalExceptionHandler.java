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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e){
        var errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            CommentNotFoundException.class,
            PostNotFoundException.class,
            UserNotFoundException.class,
            CommunityNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        var errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            UserTooYoungException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        var errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(
                                fieldError.getDefaultMessage(),
                                "Validation failed"
                        ),
                        (first, second) -> first
                ));

        var errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .time(LocalDateTime.now())
                .url(request.getRequestURL().toString())
                .build();

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataIntegrityViolationException ex, HttpServletRequest request) {
        
        var errorResponse = ErrorResponse.builder()
                .message("Db error")
                .status(HttpStatus.CONFLICT.value())
                .time(LocalDateTime.now())
                .url(request.getRequestURL().toString())
                .errors(Map.of("details", ex.getMostSpecificCause().getMessage()))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }



    @Data
    @Builder
    public static class ErrorResponse {
        private String message;
        private int status;
        private LocalDateTime time;
        private String url;
        private Map<String, String> errors;
    }
}
