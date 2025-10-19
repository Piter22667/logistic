package com.example.demo.mapper;

import com.example.demo.dto.response.DriversResponseDto;
import com.example.demo.dto.reuqest.DriverRequestDto;
import com.example.demo.entity.Driver;
import com.example.demo.entity.User;

import java.time.LocalDate;

public class DriverMapper {
    public static DriversResponseDto toDto(Driver driver) {
        DriversResponseDto driversResponseDto = new DriversResponseDto();
        driversResponseDto.setId(driver.getId());
        driversResponseDto.setEmail(driver.getUser().getEmail());
        driversResponseDto.setFirstName(driver.getFirstName());
        driversResponseDto.setLastName(driver.getLastName());
        driversResponseDto.setLicenseNumber(driver.getLicenseNumber());
        driversResponseDto.setPhone(driver.getPhone());
        driversResponseDto.setStatus(driver.getStatus());
        driversResponseDto.setHireDate(driver.getHireDate());
        return driversResponseDto;
    }

    public static Driver toModel(DriverRequestDto driverRequestDto, User user) {
        Driver driver = new Driver();

        driver.setUser(user);

        driver.setFirstName(driverRequestDto.getFirstName());
        driver.setLastName(driverRequestDto.getLastName());
        driver.setLicenseNumber(driverRequestDto.getLicenseNumber());
        driver.setPhone(driverRequestDto.getWorkPhone());
        driver.setStatus(driverRequestDto.getStatus());
        driver.setHireDate(driverRequestDto.getHireDate() != null ? LocalDate.parse(driverRequestDto.getHireDate()) : null);

        return driver;
    }
}
