package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    List<Warehouse> findByIsActiveTrue();
    
    List<Warehouse> findByCity(String city);
    
    List<Warehouse> findByState(String state);
}