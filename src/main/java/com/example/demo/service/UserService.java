package com.example.demo.service;

import com.example.demo.dto.response.AuthResponseDto;
import com.example.demo.dto.reuqest.LoginRequestDto;
import com.example.demo.dto.reuqest.RegisterRequestDto;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.UserNotFoundByEmailException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jWTService;

    public AuthResponseDto registerUser(RegisterRequestDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailAlreadyExistsException("User with email " + registerDto.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(registerDto.getEmail())
                .passwordHash(passwordEncoder.encode(registerDto.getPassword()))
                .role(UserRole.ROLE_CLIENT)
                .fullName(registerDto.getFullName())
                .companyName(registerDto.getCompanyName())
                .phone(registerDto.getPhone())
                .address(registerDto.getAddress())
                .isActive(true)
                .build();

        userRepository.save(user);

        String token = jWTService.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().toString())
                .message("User registered successfully")
                .build();
    }

    public AuthResponseDto authenticateUser(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jWTService.generateToken(loginDto.getEmail());

            // Get user details
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UserNotFoundByEmailException("User not found with email" + loginDto.getEmail()));

            return AuthResponseDto.builder()
                    .token(token)
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .message("Login successful")
                    .build();
        }

        throw new AuthenticationException("Authentication failed") {
        };
    }
}
