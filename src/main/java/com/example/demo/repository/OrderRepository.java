package com.example.demo.repository;

import com.example.demo.dto.response.UserOrderWithRouteDto;
import com.example.demo.dto.response.UserOrdersDto;
import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
    SELECT new com.example.demo.dto.response.UserOrdersDto(
        o.id,
        o.originAddress,
        o.destinationAddress,
        o.cost,
        o.createdAt,
        o.status
    )
    FROM Order o
    WHERE o.client.email = :email
        ORDER BY o.createdAt DESC
    """)
    Page<UserOrdersDto> findOrdersWithCurrentStatusByClientEmail(@Param("email") String email, Pageable pageable);

    @Query("""
    SELECT new com.example.demo.dto.response.UserOrderWithRouteDto(
        o.id,
        o.originAddress,
        o.destinationAddress,
        o.cost,
        o.routePolyline,
        o.createdAt,
        o.status
    )
    FROM Order o
    WHERE (:status IS NULL OR o.status = :status)
        ORDER BY o.createdAt DESC
""")
    Page<UserOrderWithRouteDto> findOrdersByStatus(@Param("status") OrderStatus status, Pageable pageable);

    Optional<Order> findById(Long id);

}
