package com.example.demo.exception;

public class UserDoNoHaveOrdersYetException extends RuntimeException {
    public UserDoNoHaveOrdersYetException(String message) {
        super(message);
    }
}
