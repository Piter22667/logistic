package com.example.demo.mapper;

import com.example.demo.dto.response.UserDetailsResponseDto;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDetailsResponseDto ToUserDetailsResponseDto(User user) {
        return UserDetailsResponseDto.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .companyName(user.getCompanyName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
