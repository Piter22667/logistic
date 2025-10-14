package com.example.demo.entity;

import com.example.demo.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // RELATIONSHIP: Driver → User (1:1, owning side)
    // ============================================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "license_number", nullable = false, unique = true, length = 100)
    private String licenseNumber;

    @Column(nullable = false, length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================
    // RELATIONSHIP: Driver → Trips (1:N, bidirectional)
    // ============================================
    @OneToMany(mappedBy = "driver")
    @Builder.Default
    private List<Trip> trips = new ArrayList<>();
}
