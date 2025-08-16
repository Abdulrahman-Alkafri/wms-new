package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    
    List<Zone> findByWarehouseId(Long warehouseId);
    
    List<Zone> findByCategoryId(Long categoryId);
    
    List<Zone> findByWarehouseIdAndCategoryId(Long warehouseId, Long categoryId);
}