package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>,JpaSpecificationExecutor<Shipment> {
    
}