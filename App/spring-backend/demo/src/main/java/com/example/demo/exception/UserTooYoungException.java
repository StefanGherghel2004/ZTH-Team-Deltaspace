package com.example.demo.exception;

public class UserTooYoungException extends RuntimeException {
    public UserTooYoungException(int minAge) {
        super("The minimum age is " + minAge);
    }
}
