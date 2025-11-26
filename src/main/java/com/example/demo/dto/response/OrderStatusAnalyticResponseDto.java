package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusAnalyticResponseDto {
    private String status;
    private Long orderCount;
    private BigDecimal percentage;
    private BigDecimal totalRevenue;
    private BigDecimal avgOrderValue;
}
