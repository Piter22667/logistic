package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByClient(User clientId);

    Optional<Order> findByStatus(OrderStatus status);
}
