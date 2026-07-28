package org.example.exceptions;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
}
