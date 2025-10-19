package com.example.demo.dto.reuqest;

import com.example.demo.enums.CargoType;
import com.example.demo.enums.TrailerType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequestDto {

    // Валідація через Bean Validation
    @NotNull(message = "Cargo type is required")
    private CargoType cargoType;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    @DecimalMax(value = "50000", message = "Weight cannot exceed 50 tons")
    private BigDecimal cargoWeightKg;

    @Size(max = 500, message = "Description too long")
    private String cargoDescription;

    @NotNull(message = "Trailer type is required")
    private TrailerType trailerType;

    @NotBlank(message = "Origin address is required")
    @Size(min = 5, max = 500)
    private String originAddress;

    // Координати (опціонально для першої версії)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal originLatitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal originLongitude;

    @NotBlank(message = "Destination address is required")
    private String destinationAddress;

    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;

    // Розрахунки (поки що клієнт вводить вручну або ми розраховуємо)
    @NotNull
    @DecimalMin(value = "0.1")
    private BigDecimal distanceKm;

    @NotNull
    @Min(value = 1)
    private Integer estimatedDurationMinutes;

    @FutureOrPresent(message = "Pickup date must be in future")
    private LocalDateTime scheduledPickupDate;
}
