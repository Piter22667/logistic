package com.example.demo.exception;

public class DriverAlreadyExistsWithLicenseException extends RuntimeException {
    public DriverAlreadyExistsWithLicenseException(String message) {
        super(message);
    }
}
