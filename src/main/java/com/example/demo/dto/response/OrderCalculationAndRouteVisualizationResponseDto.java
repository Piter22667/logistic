package com.example.demo.dto.response;

import com.example.demo.dto.util.PriceBreakdownDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCalculationAndRouteVisualizationResponseDto {

    private Double pickupLongitude;
    private Double pickupLatitude;

    private Double deliveryLongitude;
    private Double deliveryLatitude;

    private Object routePolyline;

    private Double distanceKm;
    private Integer durationMinutes;

    private BigDecimal calculatedPrice;

    private PriceBreakdownDto priceBreakdown;

}
