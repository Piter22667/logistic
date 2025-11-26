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
public class DynamicAnalyticDto {
    private String period;
    private Long orderCount;
    private BigDecimal revenue;
    private BigDecimal averageOrderValue;
    private BigDecimal completedRate;
}

