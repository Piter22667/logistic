package com.example.demo.controller;

import com.example.demo.dto.response.TrailerForCargoTypeFromClientResponseDto;
import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.UpdateVehicleDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.enums.CargoType;
import com.example.demo.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/trailerTypeByCargo/{cargoType}")
    public ResponseEntity<String> getTrailerByCargoType(@PathVariable("cargoType") String cargoType) {
        String recommendedTrailers = vehicleService.getTrailerByCargoTypeForClient(CargoType.valueOf(cargoType));
        return ResponseEntity.ok(recommendedTrailers);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/trailerTypeByCargoAdmin/{cargoType}")
    public ResponseEntity<List<TrailerForCargoTypeFromClientResponseDto>> getAvailableVehiclesByCargo(@PathVariable("cargoType") String cargoType) {
        List<TrailerForCargoTypeFromClientResponseDto> dtos = vehicleService.getAllTrailersByCargoTypeFromClient(CargoType.valueOf(cargoType));
        return ResponseEntity.ok(dtos);
    }
}
