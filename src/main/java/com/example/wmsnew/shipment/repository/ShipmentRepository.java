package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
}