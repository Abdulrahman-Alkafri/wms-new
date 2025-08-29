package com.example.wmsnew.inventory.repository;

import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.warehouse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    boolean existsByBatchNumber(String batchNumber);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.quantity > :quantity ORDER BY i.expiryDate ASC NULLS LAST, i.location.locationCode ASC")
    List<Inventory> findByProductIdAndQuantityGreaterThan(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    List<Inventory> findByProductAndLocationAndBatchNumber(Product product, Location location, String batchNumber);
}
