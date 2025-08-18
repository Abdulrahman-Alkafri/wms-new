package com.example.wmsnew.order.repository;

import com.example.wmsnew.common.enums.OrderStatus;
import com.example.wmsnew.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
    
}