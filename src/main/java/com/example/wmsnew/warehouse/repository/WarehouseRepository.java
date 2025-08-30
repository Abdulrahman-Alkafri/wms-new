package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WarehouseRepository
    extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
    
    // Analytics queries for warehouse statistics
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE SIZE(w.locations) > :threshold")
    Long countWarehousesWithManyLocations(@Param("threshold") Integer threshold);
    
    @Query("SELECT AVG(SIZE(w.locations)) FROM Warehouse w")
    Double getAverageWarehouseLocationCount();
    
    @Query("SELECT SUM(SIZE(w.locations)) FROM Warehouse w")
    Long getTotalWarehouseLocations();
}
