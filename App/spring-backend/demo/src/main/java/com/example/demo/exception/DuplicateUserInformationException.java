package com.example.demo.exception;

public class DuplicateUserInformationException extends RuntimeException {
    public DuplicateUserInformationException(String message) {
        super(message);
    }
}
