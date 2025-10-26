package com.example.demo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final Long orderId;
    private final Long clientId;
    private final String cargoType;
    private final String status;

    public OrderCreatedEvent(Object source, Long orderId, Long clientId, String cargoType, String status) {
        super(source);
        this.orderId = orderId;
        this.clientId = clientId;
        this.cargoType = cargoType;
        this.status = status;
    }
}

