package org.example.exceptions;

public class InvalidUserAccountException extends Exception {
    public InvalidUserAccountException() {

      super("Invalid credentials.");
    }
}
