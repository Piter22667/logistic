package com.example.demo.service;

import com.example.demo.dto.response.TrailerForCargoTypeFromClientResponseDto;
import com.example.demo.dto.response.VehicleResponseDto;
import com.example.demo.dto.reuqest.UpdateVehicleDto;
import com.example.demo.dto.reuqest.VehicleRequestDto;
import com.example.demo.entity.Vehicle;
import com.example.demo.enums.CargoType;
import com.example.demo.enums.TrailerType;
import com.example.demo.enums.VehicleStatus;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.exception.VehicleWithWithLicenseAlreadyExist;
import com.example.demo.mapper.VehicleMapper;
import com.example.demo.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RecommendationService recommendationService;

    public VehicleService(VehicleRepository vehicleRepository, RecommendationService recommendationService) {
        this.vehicleRepository = vehicleRepository;
        this.recommendationService = recommendationService;
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

    public String getTrailerByCargoTypeForClient(CargoType cargoType){
        List<String> vehicles = recommendationService.getTrailerByCargoType(String.valueOf(cargoType));

        if(vehicles.isEmpty()){
            throw new VehicleNotFoundException("No recommended trailer types found for cargo type: " + cargoType);
        }

        return vehicles.get(0);
    }

    public List<TrailerForCargoTypeFromClientResponseDto> getAllTrailersByCargoTypeFromClient(CargoType cargoType){
        String trailerTypeRequired = recommendationService.getTrailerByCargoType(cargoType.name()).get(0);

        TrailerType trailerTypeEnum = TrailerType.valueOf(trailerTypeRequired);

        List<Vehicle> vehicles = vehicleRepository.findByTrailerTypeAndStatus(trailerTypeEnum, VehicleStatus.AVAILABLE);

        if(vehicles == null || vehicles.isEmpty()){
            throw new VehicleNotFoundException("No available vehicles found for trailer type: " + trailerTypeRequired);
        }

        return VehicleMapper.toTrailerForCargoTypeFromClientDto(vehicles);
    }


}
