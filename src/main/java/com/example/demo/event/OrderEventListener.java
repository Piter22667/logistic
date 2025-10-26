package com.example.demo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventListener {

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Нове замовлення #{} створено клієнтом #{}. Тип вантажу: {}. Статус: {}",
                event.getOrderId(),
                event.getClientId(),
                event.getCargoType(),
                event.getStatus());

        // Тут можна додати:
        // - Відправку email/SMS адміністратору
        // - WebSocket сповіщення
        // - Push notification
        // - Запис в чергу повідомлень (RabbitMQ/Kafka)
    }
}
//package com.example.demo.event;
//
//import lombok.Getter;
//import org.springframework.context.ApplicationEvent;
//
//@Getter
//public class OrderCreatedEvent extends ApplicationEvent {
//    private final Long orderId;
//    private final Long clientId;
//    private final String cargoType;
//    private final String status;
//
//    public OrderCreatedEvent(Object source, Long orderId, Long clientId, String cargoType, String status) {
//        super(source);
//        this.orderId = orderId;
//        this.clientId = clientId;
//        this.cargoType = cargoType;
//        this.status = status;
//    }


