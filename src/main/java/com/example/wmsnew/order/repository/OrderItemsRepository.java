package com.example.wmsnew.order.repository;

import com.example.wmsnew.order.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
    
    List<OrderItems> findByOrderId(Long orderId);
    
    List<OrderItems> findByProductId(Long productId);
    
    List<OrderItems> findByOrderIdAndProductId(Long orderId, Long productId);
    
    @Query("SELECT SUM(oi.requestedQuantity) FROM OrderItems oi WHERE oi.product.id = :productId")
    Integer getTotalRequestedQuantityByProductId(@Param("productId") Long productId);
}