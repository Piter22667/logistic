package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserOrderProjection {
    Long getId();
    String getOriginAddress();
    String getDestinationAddress();
    BigDecimal getCost();
    LocalDateTime getCreatedAt();
    String getStatus();
}
