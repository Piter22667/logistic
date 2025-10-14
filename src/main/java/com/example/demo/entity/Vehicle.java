package com.example.demo.entity;

import com.example.demo.enums.TrailerType;
import com.example.demo.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 50)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trailer_type", nullable = false, length = 100)
    private TrailerType trailerType;

    @Column(name = "capacity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacityKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @ColumnDefault("'AVAILABLE'")
    private VehicleStatus status;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "vehicle")
    @Builder.Default
    private List<Trip> trips = new ArrayList<>();
}
