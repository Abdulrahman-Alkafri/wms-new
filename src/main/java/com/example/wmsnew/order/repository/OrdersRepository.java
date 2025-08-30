package com.example.wmsnew.order.repository;

import com.example.wmsnew.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Integer id);
    
    // Analytics queries for dashboard
    @Query("SELECT DATE(o.createdAt) as date, COUNT(o) as count, SUM(o.totalPrice) as totalValue " +
           "FROM Order o " +
           "WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY DATE(o.createdAt)")
    List<Object[]> findOrderAnalyticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    Long countOrdersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    Long sumOrderValueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT YEAR(o.createdAt) as year, MONTH(o.createdAt) as month, COUNT(o) as count, SUM(o.totalPrice) as totalValue " +
           "FROM Order o " +
           "WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
           "ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> findMonthlyOrderAnalytics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT YEAR(o.createdAt) as year, COUNT(o) as count, SUM(o.totalPrice) as totalValue " +
           "FROM Order o " +
           "WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "GROUP BY YEAR(o.createdAt) " +
           "ORDER BY YEAR(o.createdAt)")
    List<Object[]> findYearlyOrderAnalytics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}