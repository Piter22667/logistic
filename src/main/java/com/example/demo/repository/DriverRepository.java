package com.example.demo.repository;

import com.example.demo.entity.Driver;
import com.example.demo.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByStatus(DriverStatus status);
}
