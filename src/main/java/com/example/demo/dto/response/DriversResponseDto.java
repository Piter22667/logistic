package com.example.demo.dto.response;

import com.example.demo.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriversResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String phone;
    private DriverStatus status;
    private LocalDate hireDate;
}
