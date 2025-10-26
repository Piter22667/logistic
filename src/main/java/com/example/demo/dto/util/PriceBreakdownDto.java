package com.example.demo.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceBreakdownDto {
    private Double baseRate;
    private Double distanceKm;
    private Double perKmRate;
    private Double distanceCost;
    private BigDecimal weightKg;
    private Double weightCost;
    private Double cargoMultiplier;
    private Double urgentMultiplier;
    private Double total;
}

