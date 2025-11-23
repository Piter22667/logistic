package com.example.demo.controller;

import com.example.demo.dto.response.UserDetailsResponseDto;
import com.example.demo.dto.response.UserOrdersDto;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserInfoServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserInfoServiceImpl userInfoServiceImpl;
    private final OrderService orderService;

    public UserController(UserInfoServiceImpl userInfoServiceImpl, OrderService orderService) {
        this.userInfoServiceImpl = userInfoServiceImpl;
        this.orderService = orderService;
    }

    @GetMapping("/details")
    public ResponseEntity<UserDetailsResponseDto> getUserDetails(Authentication authentication) {
        String email = authentication.getName();
        UserDetailsResponseDto userDetailsResponseDto = userInfoServiceImpl.getUserByEmail(email);

        return ResponseEntity.ok(userDetailsResponseDto);

    }

    @GetMapping("/orders/list")
    public ResponseEntity<Page<UserOrdersDto>> getUserOrders(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        Page<UserOrdersDto> userOrders = orderService.getUserOrders(email, pageable);
        return ResponseEntity.ok(userOrders);
    }
}
