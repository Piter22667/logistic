package com.example.demo.dto.reuqest;

import com.example.demo.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDriverStatusDto {
    private DriverStatus driverStatus;
}
