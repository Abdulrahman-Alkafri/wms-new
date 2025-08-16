package com.example.wmsnew.inventory.repository;

import com.example.wmsnew.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    List<Inventory> findByProductId(Long productId);
    
    List<Inventory> findByLocationId(Long locationId);
    
    List<Inventory> findByBatchNumber(String batchNumber);
    
    List<Inventory> findByProductIdAndLocationId(Long productId, Long locationId);
    
    List<Inventory> findByExpiryDateBefore(LocalDate date);
    
    List<Inventory> findByQuantityGreaterThan(Integer quantity);
    
    @Query("SELECT i FROM Inventory i WHERE i.location.zone.warehouse.id = :warehouseId")
    List<Inventory> findByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") Long productId);
}