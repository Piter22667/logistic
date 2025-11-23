package com.example.demo.service;

import com.example.demo.dto.response.UserDetailsResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserInfoService {
    UserDetailsResponseDto getUserByEmail(String email);
}
