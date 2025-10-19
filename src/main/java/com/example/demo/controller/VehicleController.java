package com.example.demo.controller;

import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.UpdateVehicleDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<VehicleResponseDto>> getAllVehicles() {
        List<VehicleResponseDto> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable("id") Long id) {
        VehicleResponseDto vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDto> createVehicle(@RequestBody VehicleRequestDto vehicleRequestDto) {
        VehicleResponseDto createdVehicle = vehicleService.createVehicle(vehicleRequestDto);
        return ResponseEntity.ok(createdVehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(@PathVariable("id") Long id,
                                                            @RequestBody UpdateVehicleDto updateVehicleDto) {
        VehicleResponseDto updatedVehicle = vehicleService.updateVehicle(id, updateVehicleDto);
        return ResponseEntity.ok(updatedVehicle);
    }
}
