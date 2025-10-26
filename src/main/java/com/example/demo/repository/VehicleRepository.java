package com.example.demo.repository;

import com.example.demo.entity.Vehicle;
import com.example.demo.enums.TrailerType;
import com.example.demo.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByTrailerType(TrailerType trailerType);

    List<Vehicle> findByTrailerTypeAndStatus(TrailerType trailerType, VehicleStatus status);


    Optional<Vehicle> findByStatus(VehicleStatus status);

    boolean existsByLicensePlate(String licensePlate);
}
