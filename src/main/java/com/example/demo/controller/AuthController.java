package com.example.demo.controller;

import com.example.demo.dto.response.AuthResponseDto;
import com.example.demo.dto.reuqest.LoginRequestDto;
import com.example.demo.dto.reuqest.RegisterRequestDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerDto){
        AuthResponseDto response = userService.registerUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto){
        AuthResponseDto response = userService.authenticateUser(loginDto);
        return ResponseEntity.ok(response);
    }
}
