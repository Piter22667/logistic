package com.example.demo.dto.reuqest;

import com.example.demo.enums.CargoType;
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
public class CalculateOrderAndVisualizeRouteRequestDto {

    private String pickupAddress;

    private String deliveryAddress;

    private CargoType cargoType;

    private BigDecimal cargoWeightKg;

    /*TODO врахувати TRAILER TYPE,
    TODO sender name, recipient, phone поки не обовязкові для обрахунку та
    TODO візуалізації маршруту
    */
    private LocalDate scheduledPickupDate;
}
