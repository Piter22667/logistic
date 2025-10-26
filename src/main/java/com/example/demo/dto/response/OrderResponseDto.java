package com.example.demo.dto.response;

import com.example.demo.dto.util.PriceBreakdownDto;
import com.example.demo.entity.contactInfo.ContactInfo;
import com.example.demo.enums.CargoType;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.TrailerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private Double totalPrice;
    private OrderStatus status;
    private TrailerType trailerType;
    private CargoType cargoType;
    private LocalDate createdAt;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMinutes;
    private PriceBreakdownDto priceBreakdown;


    private ContactInfo senderContactInfo;
    private ContactInfo recipientContactInfo;
}

/*
    private Long id;
    private Long clientId;
    private String status;
    private Double totalPrice;
    private Map<String,Object> priceBreakdown;
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private List<String> recommendedTrailerTypes;
    private Instant createdAt;
 */

