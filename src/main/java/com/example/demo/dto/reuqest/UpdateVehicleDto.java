package com.example.demo.dto.reuqest;

import com.example.demo.enums.VehicleStatus;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleDto {

    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @PastOrPresent(message = "Дата останнього обслуговування не може бути в майбутньому")
    private LocalDate lastMaintenanceDate;

}
