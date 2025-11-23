package com.example.demo.mapper;

import com.example.demo.dto.response.TrailerForCargoTypeFromClientResponseDto;
import com.example.demo.dto.response.VehicleForOrderAssigningDto;
import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.entity.Vehicle;

import java.util.List;
import java.util.stream.Collectors;

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

    public static VehicleForOrderAssigningDto toVehicleForOrderAssigningDto(Vehicle vehicle) {
        VehicleForOrderAssigningDto vehicleResponseDto = new VehicleForOrderAssigningDto();
        vehicleResponseDto.setId(vehicle.getId());
        vehicleResponseDto.setLicensePlate(vehicle.getLicensePlate());
        vehicleResponseDto.setTrailerType(String.valueOf(vehicle.getTrailerType()));
        vehicleResponseDto.setStatus(String.valueOf(vehicle.getStatus()));
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

    public static List<TrailerForCargoTypeFromClientResponseDto> toTrailerForCargoTypeFromClientDto(List<Vehicle> vehicles) {
        return vehicles.stream().map(v -> TrailerForCargoTypeFromClientResponseDto.builder()
                .id(v.getId())
                .licensePlate(v.getLicensePlate())
                .trailerType(v.getTrailerType())
                .capacityKg(v.getCapacityKg())
                .manufactureYear(v.getManufactureYear())
                .build()
        ).collect(Collectors.toList());
    }
}
