package com.example.wmsnew.order.repository;

import com.example.wmsnew.order.entity.OrderPicking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPickingRepository extends JpaRepository<OrderPicking, Long> {
    List<OrderPicking> findByOrderItemId(Integer orderItemId);
}