package com.example.demo.dto.response;

import com.example.demo.enums.CargoType;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.TrailerType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateOrderOrderResponseDto {

    private Long id;

    // Інформація про клієнта (частково)
    private Long clientId;
    private String clientName;
    private String clientCompany;

    // Деталі вантажу
    private CargoType cargoType;
    private BigDecimal cargoWeightKg;
    private String cargoDescription;
    private TrailerType trailerType;

    // Маршрут
    private String originAddress;
    private String destinationAddress;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMinutes;

    // Фінанси
    private BigDecimal cost;

    // Статус
    private OrderStatus status;

    // Часові мітки
    private LocalDateTime createdAt;
    private LocalDateTime scheduledPickupDate;

//    // Рейс (якщо призначений)
//    private TripResponse trip; // Nested DTO

    // Computed fields
    public String getStatusLabel() {
        return switch (status) {
            case PENDING -> "Очікує призначення";
            case ASSIGNED -> "Призначено водія";
            case IN_TRANSIT -> "В дорозі";
            case COMPLETED -> "Завершено";
            case CANCELLED -> "Скасовано";
        };
    }
}
