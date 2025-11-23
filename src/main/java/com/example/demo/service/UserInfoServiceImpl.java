package com.example.demo.service;

import com.example.demo.dto.response.UserDetailsResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundByEmailException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserInfoServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetailsResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException("User not found"));

        return userMapper.ToUserDetailsResponseDto(user);
    }
}
