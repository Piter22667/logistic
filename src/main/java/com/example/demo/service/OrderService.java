package com.example.demo.service;

import com.example.demo.dto.response.*;
import com.example.demo.dto.reuqest.CalculateOrderAndVisualizeRouteRequestDto;
import com.example.demo.dto.reuqest.CreateOrderRequestDto;
import com.example.demo.dto.util.PriceBreakdownDto;
import com.example.demo.entity.*;
import com.example.demo.entity.contactInfo.ContactInfo;
import com.example.demo.enums.*;
import com.example.demo.exception.IncorrectPickUpDateException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.VehicleMapper;
import com.example.demo.repository.*;
import com.example.demo.util.PriceCalculator;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final RecommendationService recommendationService;
    private final MapBoxService mapBoxService;
    private final PriceCalculator priceCalculator;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, OrderStatusHistoryRepository orderStatusHistoryRepository, RecommendationService recommendationService, MapBoxService mapBoxService, PriceCalculator priceCalculator, VehicleRepository vehicleRepository, DriverRepository driverRepository, TripRepository tripRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.recommendationService = recommendationService;
        this.mapBoxService = mapBoxService;
        this.priceCalculator = priceCalculator;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.emailService = emailService;
    }

    public OrderCalculationAndRouteVisualizationResponseDto calculateOrderAndRoute(
            CalculateOrderAndVisualizeRouteRequestDto requestDto) {

        Point pickupPoint = mapBoxService.geocodeAddress(requestDto.getPickupAddress());
        Point deliveryPoint = mapBoxService.geocodeAddress(requestDto.getDeliveryAddress());

        DirectionsRoute route = mapBoxService.getRoute(pickupPoint, deliveryPoint);

        mapBoxService.testRoute(requestDto.getPickupAddress(), requestDto.getDeliveryAddress());


        double distanceKm = route.distance() / 1000.0;
        int durationInMinutes = (int) Math.round(route.duration() / 60.0);

        List<String> recommendedTrailers = recommendationService.getTrailerByCargoType(
                requestDto.getCargoType().name()
        );
        TrailerType trailerType = TrailerType.valueOf(recommendedTrailers.get(0));

        boolean urgent = requestDto.getScheduledPickupDate() != null &&
                requestDto.getScheduledPickupDate().atStartOfDay().isBefore(LocalDateTime.now().plusHours(6));

        PriceBreakdownDto priceBreakdown = priceCalculator.calculate(
                requestDto.getCargoType(),
                trailerType,
                distanceKm,
                requestDto.getCargoWeightKg(),
                urgent
        );

        return OrderCalculationAndRouteVisualizationResponseDto.builder()
                .pickupLongitude(pickupPoint.longitude())
                .pickupLatitude(pickupPoint.latitude())
                .deliveryLongitude(deliveryPoint.longitude())
                .deliveryLatitude(deliveryPoint.latitude())
                .routePolyline(route.geometry())
                .distanceKm(distanceKm)
                .durationMinutes(durationInMinutes)
                .calculatedPrice(BigDecimal.valueOf(priceBreakdown.getTotal()))
                .priceBreakdown(priceBreakdown)
                .build();
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequestDto orderRequestDto, Long clientId) {

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (orderRequestDto.getScheduledPickupDate() != null) {
            LocalDate minTimeAllowed = LocalDate.from(LocalDateTime.now().plusDays(1));
            if (orderRequestDto.getScheduledPickupDate().isBefore(minTimeAllowed)) {
                throw new IncorrectPickUpDateException("ScheduledPickupDate must be at least " + 2 + " hours from now");
            }
        }

        if (orderRequestDto.getCargoWeightKg() != null && orderRequestDto.getCargoWeightKg().doubleValue() <= 0) {
            throw new IllegalArgumentException("Cargo weight must be greater than zero");
        }

        Point pickupPoint = mapBoxService.geocodeAddress(orderRequestDto.getOriginAddress());
        Point deliveryPoint = mapBoxService.geocodeAddress(orderRequestDto.getDestinationAddress());

        double orderPickUpLongitude = pickupPoint.longitude();
        double orderPickUpLatitude = pickupPoint.latitude();

        double orderDeliveryLongitude = deliveryPoint.longitude();
        double orderDeliveryLatitude = deliveryPoint.latitude();

        log.info("Pick up address: {}, destination address: {}", pickupPoint, deliveryPoint);

        DirectionsRoute route = mapBoxService.getRoute(pickupPoint, deliveryPoint);

        mapBoxService.testRoute(orderRequestDto.getOriginAddress(), orderRequestDto.getDestinationAddress());

        double distanceKm = route.distance() / 1000.0;
        int durationInMinutes = (int) Math.round(route.duration() / 60.0);


        List<String> recommendedTrailers = recommendationService.getTrailerByCargoType(
                orderRequestDto.getCargoType().name()
        );
        TrailerType selectedTrailer = TrailerType.valueOf(recommendedTrailers.get(0));

        boolean urgent = orderRequestDto.getScheduledPickupDate() != null &&
                orderRequestDto.getScheduledPickupDate().atStartOfDay().isBefore(LocalDateTime.now().plusHours(6));

        PriceBreakdownDto priceBreakdown = priceCalculator.calculate(
                orderRequestDto.getCargoType(),
                selectedTrailer,
                distanceKm,
                orderRequestDto.getCargoWeightKg(),
                urgent
        );

        double total = priceBreakdown.getTotal();

        ContactInfo senderContactInfo = orderRequestDto.getSenderContactInfo();
        ContactInfo recipientContactInfo = orderRequestDto.getRecipientContactInfo();

        Order order = new Order();
        order.setClient(client);
        order.setCargoType(orderRequestDto.getCargoType());
        order.setCargoWeightKg(orderRequestDto.getCargoWeightKg());
        order.setCargoDescription(orderRequestDto.getCargoDescription());
        order.setTrailerType(TrailerType.valueOf(String.valueOf(selectedTrailer)));

        order.setOriginAddress(orderRequestDto.getOriginAddress());
        order.setOriginLatitude(BigDecimal.valueOf(orderPickUpLatitude));
        order.setOriginLongitude(BigDecimal.valueOf(orderPickUpLongitude));

        order.setDestinationAddress(orderRequestDto.getDestinationAddress());
        order.setDestinationLatitude(BigDecimal.valueOf(orderDeliveryLatitude));
        order.setDestinationLongitude(BigDecimal.valueOf(orderDeliveryLongitude));

        order.setRoutePolyline(route.geometry());

        order.setSenderInfo(senderContactInfo);
        order.setReceiverInfo(recipientContactInfo);

        order.setDistanceKm(BigDecimal.valueOf(distanceKm));
        order.setEstimatedDurationMinutes(durationInMinutes);
        order.setCost(BigDecimal.valueOf(total));
        order.setStatus(OrderStatus.PENDING);
        order.setScheduledPickupDate(orderRequestDto.getScheduledPickupDate());

        orderRepository.save(order);

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setOldStatus(null);
        orderStatusHistory.setNewStatus(String.valueOf(order.getStatus()));
        orderStatusHistory.setChangedBy(client);
        orderStatusHistory.setComment("Order created by client");
        orderStatusHistoryRepository.save(orderStatusHistory);

        emailService.sendOrderCreatedEmail(client.getEmail(), order.getId(), order.getOriginAddress(), order.getDestinationAddress(), order.getScheduledPickupDate(), order.getCost());

        return OrderMapper.toDto(order, priceBreakdown, total, senderContactInfo, recipientContactInfo);
    }

    public Page<UserOrdersDto> getUserOrders(String email, Pageable pageable) {
        Page<UserOrdersDto> userOrders = orderRepository.findOrdersWithCurrentStatusByClientEmail(email, pageable);

        return userOrders;
    }

    public Page<UserOrderWithRouteDto> findOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<UserOrderWithRouteDto> orders = orderRepository.findOrdersByStatus(status, pageable);
        return orders;
    }

    public List<VehicleForOrderAssigningDto> getVehicleForOrderAssign(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId));

        TrailerType trailerType = order.getTrailerType();

        List<Vehicle> vehicles = vehicleRepository.findByTrailerTypeAndStatus(trailerType, VehicleStatus.AVAILABLE);

        return vehicles.stream().map(VehicleMapper::toVehicleForOrderAssigningDto).toList();
    }

    @Transactional
    public void assignDriverAndVehicle(Long orderId, Long driverId, Long vehicleId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId));


        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found with id: " + driverId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + vehicleId));


        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in PENDING status");
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not in AVAILABLE status");
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver is not in AVAILABLE status");
        }

        if(!vehicle.getTrailerType().equals(order.getTrailerType())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle trailer type does not match order trailer type");
        }

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.ASSIGNED);
        driver.setStatus(DriverStatus.BUSY);
        vehicle.setStatus(VehicleStatus.IN_USE);

        Trip trip = new Trip();
        trip.setOrder(order);
        trip.setDriver(driver);
        trip.setVehicle(vehicle);
        trip.setAssignedAt(LocalDateTime.now());
        trip.setStatus(TripStatus.ASSIGNED);

        tripRepository.save(trip);

        orderRepository.save(order);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);

        emailService.sendOrderStatusChangedEmail(order.getClient().getEmail(), order.getId(), oldStatus, order.getStatus(), order.getOriginAddress(), order.getDestinationAddress());
    }

    public void startOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in ASSIGNED status");
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.IN_TRANSIT);
        orderRepository.save(order);

        emailService.sendOrderStatusChangedEmail(
                order.getClient().getEmail(),
                order.getId(),
                oldStatus,
                order.getStatus(),
                order.getOriginAddress(),
                order.getDestinationAddress()
        );
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in IN_TRANSIT status");
        }

        Trip trip = tripRepository.findTripById(order.getTrip().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found for order: " + orderId));

        Driver driver = driverRepository.findById(trip.getDriver().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found for trip: " + trip.getId()));

        Vehicle vehicle = vehicleRepository.findById(trip.getVehicle().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found for trip: " + trip.getId()));

        driver.setStatus(DriverStatus.AVAILABLE);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        trip.setStatus(TripStatus.COMPLETED);


        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.COMPLETED);

        driverRepository.save(driver);
        vehicleRepository.save(vehicle);
        tripRepository.save(trip);

        orderRepository.save(order);

        emailService.sendOrderStatusChangedEmail(
                order.getClient().getEmail(),
                order.getId(),
                oldStatus,
                order.getStatus(),
                order.getOriginAddress(),
                order.getDestinationAddress()
        );
    }
}