package com.example.demo.service;

import com.example.demo.dto.response.DriversResponseDto;
import com.example.demo.dto.reuqest.DriverRequestDto;
import com.example.demo.dto.reuqest.UpdateDriverDto;
import com.example.demo.entity.Driver;
import com.example.demo.entity.User;
import com.example.demo.enums.DriverStatus;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.DriverAlreadyDeletedException;
import com.example.demo.exception.DriverAlreadyExistsWithLicenseException;
import com.example.demo.exception.DriverNotExistsException;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.mapper.DriverMapper;
import com.example.demo.repository.DriverRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<DriversResponseDto> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        List<DriversResponseDto> driversResponseDtos = drivers.stream().map(
                DriverMapper::toDto).toList();

        return driversResponseDtos;
    }

    @Transactional
    public DriversResponseDto createDriver(DriverRequestDto driverRequestDto) {

        if (userRepository.existsByEmail(driverRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use (message from service) with email: " + driverRequestDto.getEmail());
        }

        User user = new User();
        user.setEmail(driverRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(driverRequestDto.getPassword()));
        user.setRole(UserRole.DRIVER);
        user.setFullName(driverRequestDto.getFirstName() + " " + driverRequestDto.getLastName());
        user.setIsActive(true);

        userRepository.save(user);

        if (driverRepository.existsByLicenseNumber(driverRequestDto.getLicenseNumber())) {
            throw new DriverAlreadyExistsWithLicenseException("Driver with license number already exists: " + driverRequestDto.getLicenseNumber());
        }

        Driver driver = DriverMapper.toModel(driverRequestDto, user);
        driver = driverRepository.save(driver);

        return DriverMapper.toDto(driver);
    }

    public DriversResponseDto getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(
                () -> new DriverNotExistsException("Driver with id " + driverId + " does not exist")
        );
        return DriverMapper.toDto(driver);
    }

    @Transactional
    public DriversResponseDto updateDriver(Long driverId, UpdateDriverDto updateDriverDto) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(
                () -> new DriverNotExistsException("Driver with id " + driverId + " does not exist")
        );

        boolean nameChanged = false;

        if(updateDriverDto.getFirstName() != null) {
            driver.setFirstName(updateDriverDto.getFirstName());
            nameChanged = true;
        }

        if(updateDriverDto.getEmail() != null) {
            if (userRepository.existsByEmail(updateDriverDto.getEmail())) {
                throw new EmailAlreadyExistsException("Email is already in use (message from service) with email: " + updateDriverDto.getEmail());
            }
            driver.getUser().setEmail(updateDriverDto.getEmail());
        }

        if(updateDriverDto.getLastName() != null) {
            driver.setLastName(updateDriverDto.getLastName());
            nameChanged = true;
        }

        if(nameChanged) {
            String newFullName = driver.getFirstName() + " " + driver.getLastName();
            driver.getUser().setFullName(newFullName);
        }

        if (updateDriverDto.getWorkPhone() != null) {
            driver.setPhone(updateDriverDto.getWorkPhone());
        }

        if (updateDriverDto.getStatus() != null) {
            driver.setStatus(updateDriverDto.getStatus());
        }

        if (updateDriverDto.getHireDate() != null) {
            driver.setHireDate(LocalDate.parse(updateDriverDto.getHireDate()));
        }

        driver = driverRepository.save(driver);

        return DriverMapper.toDto(driver);
    }

    public void deleteDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(
                () -> new DriverNotExistsException("Водій з id " + driverId + " не існує.")
        );

        if(!driver.getUser().getIsActive()) {
            throw new DriverAlreadyDeletedException("Водій з id " + driverId + " вже видалений.");
        }

        if(driver.getStatus() == DriverStatus.ON_TRIP) {
            throw new IllegalStateException("Неможливо видалити водія, з Id: " + driverId + ", оскільки він наразі перебуває в дорозі.");
        }

        driver.getUser().setIsActive(false);
        driver.setStatus(DriverStatus.OFF_DUTY);

        driverRepository.save(driver);
    }
}
