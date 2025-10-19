package com.example.demo.dto.response;

import com.example.demo.enums.TrailerType;
import com.example.demo.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponseDto {
    private Long id;
    private String licensePlate;
    private TrailerType trailerType;
    private BigDecimal capacity;
    private VehicleStatus status;
    private int manufactureYear;

//    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastMaintenanceDate;

//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
