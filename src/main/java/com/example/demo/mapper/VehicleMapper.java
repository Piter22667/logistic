package com.example.demo.mapper;

import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.entity.Vehicle;

public class VehicleMapper {
    public static VehicleResponseDto toDto(Vehicle vehicle) {
        VehicleResponseDto vehicleResponseDto = new VehicleResponseDto();
        vehicleResponseDto.setId(vehicle.getId());
        vehicleResponseDto.setLicensePlate(vehicle.getLicensePlate());
        vehicleResponseDto.setTrailerType(vehicle.getTrailerType());
        vehicleResponseDto.setCapacity(vehicle.getCapacityKg());
        vehicleResponseDto.setStatus(vehicle.getStatus());
        vehicleResponseDto.setManufactureYear(vehicle.getManufactureYear());
        vehicleResponseDto.setLastMaintenanceDate(vehicle.getLastMaintenanceDate());
        vehicleResponseDto.setCreatedAt(vehicle.getCreatedAt());
        vehicleResponseDto.setUpdatedAt(vehicle.getUpdatedAt());

        return vehicleResponseDto;
    }

    public static Vehicle toModel(VehicleRequestDto vehicleRequestDto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(vehicleRequestDto.getLicensePlate());
        vehicle.setTrailerType(vehicleRequestDto.getTrailerType());
        vehicle.setCapacityKg(vehicleRequestDto.getCapacity());
        vehicle.setStatus(vehicleRequestDto.getStatus());
        vehicle.setManufactureYear(vehicleRequestDto.getManufactureYear());
        vehicle.setLastMaintenanceDate(vehicleRequestDto.getLastMaintenanceDate());

        return vehicle;
    }
}
