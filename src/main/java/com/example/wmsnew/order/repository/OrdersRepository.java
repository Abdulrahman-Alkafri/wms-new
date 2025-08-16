package com.example.wmsnew.order.repository;

import com.example.wmsnew.common.enums.OrderStatus;
import com.example.wmsnew.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    
    Optional<Orders> findByOrderNumber(String orderNumber);
    
    List<Orders> findByStatus(OrderStatus status);
    
    List<Orders> findByWarehouseId(Long warehouseId);
    
    List<Orders> findByCustomerId(Long customerId);
    
    List<Orders> findByWarehouseIdAndStatus(Long warehouseId, OrderStatus status);
    
    List<Orders> findByRequiredDateBefore(LocalDate date);
    
    List<Orders> findByRequiredDateBetween(LocalDate startDate, LocalDate endDate);
}