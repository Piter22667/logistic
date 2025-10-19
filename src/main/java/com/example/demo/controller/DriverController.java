package com.example.demo.controller;

import com.example.demo.dto.response.DriversResponseDto;
import com.example.demo.dto.reuqest.DriverRequestDto;
import com.example.demo.dto.reuqest.UpdateDriverDto;
import com.example.demo.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DriversResponseDto>> getAllDrivers() {
        List<DriversResponseDto> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<DriversResponseDto> getDriverById(@PathVariable Long Id) {
        DriversResponseDto driver = driverService.getDriverById(Id);
        return ResponseEntity.ok(driver);
    }

    @PostMapping
    public ResponseEntity<DriversResponseDto> createDriver(@Valid @RequestBody DriverRequestDto driverRequestDto) {
        DriversResponseDto createdDriver = driverService.createDriver(driverRequestDto);
        return ResponseEntity.ok(createdDriver);
    }

    @PutMapping("/{Id}")
    public ResponseEntity<DriversResponseDto> updateDriver(@PathVariable Long Id,
                                                           @Valid @RequestBody UpdateDriverDto updateDriverDto) {
        DriversResponseDto updatedDriver = driverService.updateDriver(Id, updateDriverDto);
        return ResponseEntity.ok(updatedDriver);
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long Id) {
        driverService.deleteDriver(Id);
        return ResponseEntity.noContent().build();
    }
}
