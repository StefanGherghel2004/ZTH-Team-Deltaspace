package com.example.demo.exception;

public class IdenticalPasswordException extends RuntimeException {
    public IdenticalPasswordException(String message) {
        super(message);
    }
}
