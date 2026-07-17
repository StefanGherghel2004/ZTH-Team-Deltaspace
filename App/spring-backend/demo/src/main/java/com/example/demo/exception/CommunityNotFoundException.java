package com.example.demo.exception;

public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }
}
