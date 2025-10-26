package com.example.demo.dto.reuqest;

import com.example.demo.entity.contactInfo.ContactInfo;
import com.example.demo.enums.CargoType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class CreateOrderRequestDto {

    @NotNull(message = "Cargo type is required")
    private CargoType cargoType;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be positive")
    @DecimalMax(value = "50000", message = "Weight cannot exceed 50 tons")
    private BigDecimal cargoWeightKg;

    @Size(max = 500, message = "Description too long")
    private String cargoDescription;

    @NotBlank(message = "Origin address is required")
    @Size(min = 5, max = 500)
    private String originAddress;

    @NotBlank(message = "Destination address is required")
    private String destinationAddress;

    @FutureOrPresent(message = "Pickup date must be in future")
    private LocalDate scheduledPickupDate;

    @NotNull(message = "Контактна інформація відправника є обов'язковою")
    @Valid
    private ContactInfo senderContactInfo;

    @NotNull(message = "Контактна інформація отримувача є обов'язковою")
    @Valid
    private ContactInfo recipientContactInfo;
}
