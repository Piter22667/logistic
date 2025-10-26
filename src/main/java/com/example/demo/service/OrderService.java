package com.example.demo.service;

import com.example.demo.dto.response.OrderCalculationAndRouteVisualizationResponseDto;
import com.example.demo.dto.response.OrderResponseDto;
import com.example.demo.dto.reuqest.CalculateOrderAndVisualizeRouteRequestDto;
import com.example.demo.dto.reuqest.CreateOrderRequestDto;
import com.example.demo.dto.util.PriceBreakdownDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatusHistory;
import com.example.demo.entity.User;
import com.example.demo.entity.contactInfo.ContactInfo;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.TrailerType;
import com.example.demo.exception.IncorrectPickUpDateException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderStatusHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.PriceCalculator;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, OrderStatusHistoryRepository orderStatusHistoryRepository, RecommendationService recommendationService, MapBoxService mapBoxService, PriceCalculator priceCalculator) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.recommendationService = recommendationService;
        this.mapBoxService = mapBoxService;
        this.priceCalculator = priceCalculator;
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

    public OrderResponseDto createOrder(CreateOrderRequestDto orderRequestDto, Long clientId) {

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(orderRequestDto.getScheduledPickupDate() != null) {
            LocalDate minTimeAllowed = LocalDate.from(LocalDateTime.now().plusDays(1));
            if(orderRequestDto.getScheduledPickupDate().isBefore(minTimeAllowed)) {
                throw new IncorrectPickUpDateException("ScheduledPickupDate must be at least " + 2 + " hours from now");
            }
        }

        if(orderRequestDto.getCargoWeightKg() != null && orderRequestDto.getCargoWeightKg().doubleValue() <= 0) {
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

         return OrderMapper.toDto(order, priceBreakdown, total, senderContactInfo, recipientContactInfo);
    }
}