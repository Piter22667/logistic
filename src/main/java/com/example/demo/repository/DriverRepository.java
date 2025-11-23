package com.example.demo.repository;

import com.example.demo.dto.response.DriverForOrderAssigningDto;
import com.example.demo.entity.Driver;
import com.example.demo.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByLicenseNumber(String licenseNumber);

    @Query("""
                SELECT new com.example.demo.dto.response.DriverForOrderAssigningDto(
                    d.id,
                    d.firstName,
                    d.lastName,
                    d.licenseNumber
                )
                FROM Driver d
                WHERE d.status = com.example.demo.enums.DriverStatus.AVAILABLE
            """)
    List<DriverForOrderAssigningDto> getDriverByStatusAvailable();

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByStatus(DriverStatus status);
}
