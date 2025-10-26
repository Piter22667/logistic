package com.example.demo.exception;

public class IncorrectPickUpDateException extends RuntimeException {
    public IncorrectPickUpDateException(String message) {
        super(message);
    }
}
