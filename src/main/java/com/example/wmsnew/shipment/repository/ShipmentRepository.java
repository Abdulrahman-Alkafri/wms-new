package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>,JpaSpecificationExecutor<Shipment> {
    
    boolean existsByShipmentNumber(String shipmentNumber);
    
    @Query("SELECT MAX(CAST(SUBSTRING(s.shipmentNumber, LENGTH(:prefix) + 1) AS INTEGER)) " +
           "FROM Shipment s WHERE s.shipmentNumber LIKE CONCAT(:prefix, '%')")
    Integer findMaxSequenceForPrefix(@Param("prefix") String prefix);
    
    // Analytics queries for dashboard
    @Query("SELECT DATE(s.createdAt) as date, COUNT(s) as count, SUM(s.totalPrice) as totalValue " +
           "FROM Shipment s " +
           "WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate " +
           "GROUP BY DATE(s.createdAt) " +
           "ORDER BY DATE(s.createdAt)")
    List<Object[]> findShipmentAnalyticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    Long countShipmentsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(s.totalPrice) FROM Shipment s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    Long sumShipmentValueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT YEAR(s.createdAt) as year, MONTH(s.createdAt) as month, COUNT(s) as count, SUM(s.totalPrice) as totalValue " +
           "FROM Shipment s " +
           "WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate " +
           "GROUP BY YEAR(s.createdAt), MONTH(s.createdAt) " +
           "ORDER BY YEAR(s.createdAt), MONTH(s.createdAt)")
    List<Object[]> findMonthlyShipmentAnalytics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT YEAR(s.createdAt) as year, COUNT(s) as count, SUM(s.totalPrice) as totalValue " +
           "FROM Shipment s " +
           "WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate " +
           "GROUP BY YEAR(s.createdAt) " +
           "ORDER BY YEAR(s.createdAt)")
    List<Object[]> findYearlyShipmentAnalytics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
