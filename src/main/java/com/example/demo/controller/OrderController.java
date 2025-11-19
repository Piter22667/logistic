package com.example.demo.controller;

import com.example.demo.dto.response.OrderCalculationAndRouteVisualizationResponseDto;
import com.example.demo.dto.response.OrderResponseDto;
import com.example.demo.dto.reuqest.CalculateOrderAndVisualizeRouteRequestDto;
import com.example.demo.dto.reuqest.CreateOrderRequestDto;
import com.example.demo.model.UserPrincipal;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderRequestDto createOrderRequestDto,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        Long userId = userPrincipal.getId();
        System.out.println("User id: " + userId);
        OrderResponseDto resp = orderService.createOrder(createOrderRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/calculate")
//    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderCalculationAndRouteVisualizationResponseDto> calculateOrder(
            @RequestBody @Valid CalculateOrderAndVisualizeRouteRequestDto requestDto) {
        OrderCalculationAndRouteVisualizationResponseDto response =
                orderService.calculateOrderAndRoute(requestDto);
        return ResponseEntity.ok(response);
    }
}
