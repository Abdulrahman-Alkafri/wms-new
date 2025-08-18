package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.shipment.entity.ShipmentItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentItemsRepository extends JpaRepository<ShipmentItems, Long> {
    
}