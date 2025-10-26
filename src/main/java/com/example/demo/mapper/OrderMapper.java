package com.example.demo.mapper;

import com.example.demo.dto.response.OrderResponseDto;
import com.example.demo.dto.util.PriceBreakdownDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.contactInfo.ContactInfo;

import java.time.LocalDate;

public class OrderMapper {
    public static OrderResponseDto toDto(Order order, PriceBreakdownDto breakdown, double totalPrice, ContactInfo senderContactInfo, ContactInfo recipientContactInfo) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());
        orderResponseDto.setClientId(order.getClient().getId());
        orderResponseDto.setCargoType(order.getCargoType());
        orderResponseDto.setTrailerType(order.getTrailerType());
        orderResponseDto.setStatus(order.getStatus());
        orderResponseDto.setTotalPrice(totalPrice);
        orderResponseDto.setCreatedAt(LocalDate.from(order.getCreatedAt()));
        orderResponseDto.setDistanceKm(order.getDistanceKm());
        orderResponseDto.setPriceBreakdown(breakdown);
        orderResponseDto.setSenderContactInfo(senderContactInfo);
        orderResponseDto.setRecipientContactInfo(recipientContactInfo);

        return orderResponseDto;
    }
}
