package com.example.demo.dto.response;

import com.example.demo.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderWithRouteDto {
    private Long id;
    private String originAddress;
    private String destinationAddress;
    private BigDecimal cost;

    private Object routePolyline;

    private LocalDateTime createdAt;
    private OrderStatus status;
}
