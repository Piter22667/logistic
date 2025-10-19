package com.example.demo.exception;

public class DriverNotExistsException extends RuntimeException {
    public DriverNotExistsException(String message) {
        super(message);
    }
}
