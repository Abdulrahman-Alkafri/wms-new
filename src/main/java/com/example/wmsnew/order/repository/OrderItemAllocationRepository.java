package com.example.wmsnew.order.repository;

import com.example.wmsnew.order.entity.OrderItemAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemAllocationRepository extends JpaRepository<OrderItemAllocation, Long> {
    List<OrderItemAllocation> findByOrderItemId(Integer orderItemId);
}