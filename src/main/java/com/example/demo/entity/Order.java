package com.example.demo.entity;

import com.example.demo.entity.contactInfo.ContactInfo;
import com.example.demo.enums.CargoType;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.TrailerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // RELATIONSHIP: Order → User (N:1, owning side)
    // ============================================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // ============================================
    // CARGO DETAILS
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo_type", nullable = false, length = 100)
    private CargoType cargoType;

    @Column(name = "cargo_weight_kg", precision = 10, scale = 2)
    private BigDecimal cargoWeightKg;

    @Column(name = "cargo_description", columnDefinition = "TEXT")
    private String cargoDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "trailer_type", nullable = false, length = 100)
    private TrailerType trailerType;

    // ============================================
    // ROUTE
    // ============================================
    @Column(name = "origin_address", nullable = false, columnDefinition = "TEXT")
    private String originAddress;

    @Column(name = "origin_latitude", precision = 10, scale = 8)
    private BigDecimal originLatitude;

    @Column(name = "origin_longitude", precision = 11, scale = 8)
    private BigDecimal originLongitude;

    @Column(name = "destination_address", nullable = false, columnDefinition = "TEXT")
    private String destinationAddress;

    @Column(name = "destination_latitude", precision = 10, scale = 8)
    private BigDecimal destinationLatitude;

    @Column(name = "destination_longitude", precision = 11, scale = 8)
    private BigDecimal destinationLongitude;

    @Column(name = "route_polyline", columnDefinition = "TEXT")
    private String routePolyline;

    // ============================================
    // CALCULATIONS
    // ============================================
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    // ============================================
    // STATUS & DATES
    // ============================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "scheduled_pickup_date")
    private LocalDate scheduledPickupDate; //TODO змінити тип поля в бд

    // додаткова інформація про отримувачів та відправників
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fullName", column = @Column(name = "sender_full_name", nullable = false)),
            @AttributeOverride(name = "phoneNumber", column = @Column(name = "sender_phone_number", nullable = false)),
            @AttributeOverride(name = "email", column = @Column(name = "sender_email"))
    })
    private ContactInfo senderInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fullName", column = @Column(name = "receiver_full_name", nullable = false)),
            @AttributeOverride(name = "phoneNumber" ,column = @Column(name = "receiver_phone_number", nullable = false)),
            @AttributeOverride(name = "email", column = @Column(name = "receiver_email"))
    })
    private ContactInfo receiverInfo;

    // ============================================
    // RELATIONSHIPS
    // ============================================

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Trip trip;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
}
