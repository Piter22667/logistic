package com.example.demo.dto.response;

import com.example.demo.enums.TrailerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrailerForCargoTypeFromClientResponseDto {

    private Long id;
    private String licensePlate;
    private BigDecimal capacityKg;
    private TrailerType trailerType;
    private Integer manufactureYear;
}
