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
public class ComparePeriodsResponseDto {
    private String metric;
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private BigDecimal changeValue;
    private BigDecimal changePercentage;
}
