package com.example.demo.exception;

public class UserNotFoundByEmailException extends RuntimeException {
    public UserNotFoundByEmailException(String message) {
        super(message);
    }
}
