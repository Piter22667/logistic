package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverForOrderAssigningDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String licenceNumber;
}
