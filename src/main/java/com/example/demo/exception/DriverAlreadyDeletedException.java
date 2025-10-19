package com.example.demo.exception;

public class DriverAlreadyDeletedException extends RuntimeException {
    public DriverAlreadyDeletedException(String message) {
        super(message);
    }
}
