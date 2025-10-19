package com.example.demo.service;

import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.UpdateVehicleDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.entity.Vehicle;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.exception.VehicleWithWithLicenseAlreadyExist;
import com.example.demo.mapper.VehicleMapper;
import com.example.demo.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleResponseDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleResponseDto> vehicleResponseDtos = vehicles.stream().map(
                VehicleMapper::toDto).toList();

        return vehicleResponseDtos;
    }

    public VehicleResponseDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        return VehicleMapper.toDto(vehicle);
    }

    public VehicleResponseDto createVehicle(VehicleRequestDto vehicleRequestDto){
        Vehicle vehicle = VehicleMapper.toModel(vehicleRequestDto);

        if(vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())){
            throw new VehicleWithWithLicenseAlreadyExist("Причеп зі вказаним номером реєстрації вже існує: " + vehicle.getLicensePlate());
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return VehicleMapper.toDto(savedVehicle);
    }

    public VehicleResponseDto updateVehicle(Long id, UpdateVehicleDto updateVehicleDto){
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicle.setStatus(updateVehicleDto.getStatus());
        vehicle.setLastMaintenanceDate(updateVehicleDto.getLastMaintenanceDate());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return VehicleMapper.toDto(updatedVehicle);
    }
}
