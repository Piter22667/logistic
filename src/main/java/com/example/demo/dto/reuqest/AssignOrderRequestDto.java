package com.example.demo.dto.reuqest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignOrderRequestDto {
    private Long driverId;
    private Long vehicleId;
}
