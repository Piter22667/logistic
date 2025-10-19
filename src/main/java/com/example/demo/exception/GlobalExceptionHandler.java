package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> map = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            map.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(map);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {

        log.warn("Email already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Email вже існує");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DriverAlreadyExistsWithLicenseException.class)
    public ResponseEntity<Map<String, String>> handleDriverAlreadyExistsWithLicenseException(DriverAlreadyExistsWithLicenseException ex) {

        log.warn("Driver with license number already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Водій зі вказаним номером ліцензії вже існує");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DriverNotExistsException.class)
    public ResponseEntity<Map<String, String>> handleDriverNotExistsException(DriverNotExistsException ex) {

        log.warn("Driver does not exist: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Водій не існує");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DriverAlreadyDeletedException.class)
    public ResponseEntity<Map<String, String>> handleDriverAlreadyDeletedException(DriverAlreadyDeletedException ex) {

        log.warn("Driver already deleted: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Водій вже видалений");
        return ResponseEntity.badRequest().body(errors);
    }

    //Vehicle exceptions

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVehicleNotFoundException(VehicleNotFoundException ex) {

        log.warn("Vehicle not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Транспортний засіб не знайдено");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(VehicleWithWithLicenseAlreadyExist.class)
    public ResponseEntity<Map<String, String>> handleVehicleWithWithLicenseAlreadyExist(VehicleWithWithLicenseAlreadyExist ex) {

        log.warn("Vehicle with license plate already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Причеп з таким номером вже існує");
        return ResponseEntity.badRequest().body(errors);
    }
}
