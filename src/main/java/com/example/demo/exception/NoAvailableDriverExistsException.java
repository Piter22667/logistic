package com.example.demo.exception;

public class NoAvailableDriverExistsException extends RuntimeException {
    public NoAvailableDriverExistsException(String message) {
        super(message);
    }
}
