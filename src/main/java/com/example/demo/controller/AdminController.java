package com.example.demo.controller;

import com.example.demo.dto.response.*;
import com.example.demo.dto.reuqest.AssignOrderRequestDto;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.DriverService;
import com.example.demo.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final OrderService orderService;
    private final DriverService driverService;

    public AdminController(OrderService orderService, DriverService driverService) {
        this.orderService = orderService;
        this.driverService = driverService;
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserOrderWithRouteDto>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        Page<UserOrderWithRouteDto> orders = orderService.findOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("drivers/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverForOrderAssigningDto>> getAvailableDriver() {
        List<DriverForOrderAssigningDto> driver = driverService.getDriverByStatusAvailable();
        return ResponseEntity.ok(driver);
    }

    @GetMapping("vehicles/available/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleForOrderAssigningDto>> getAvailableVehicle(@PathVariable Long orderId) {
        List<VehicleForOrderAssigningDto> vehicle = orderService.getVehicleForOrderAssign(orderId);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping("orders/assign/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignOrder( @PathVariable Long orderId, @RequestBody AssignOrderRequestDto assignOrderRequestDto) {
        orderService.assignDriverAndVehicle(orderId, assignOrderRequestDto.getDriverId(), assignOrderRequestDto.getVehicleId());
        return ResponseEntity.ok().body("{\"message\": \"Order assigned successfully\"}");
    }

    @PostMapping("/orders/{orderId}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> startOrder(@PathVariable Long orderId) {
        orderService.startOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
