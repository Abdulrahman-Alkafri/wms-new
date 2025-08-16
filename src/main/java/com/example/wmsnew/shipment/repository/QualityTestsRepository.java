package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.common.enums.QualityTestStatus;
import com.example.wmsnew.shipment.entity.QualityTests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityTestsRepository extends JpaRepository<QualityTests, Long> {
    
    List<QualityTests> findByShipmentId(Long shipmentId);
    
    List<QualityTests> findByTestedById(Long testedById);
    
    List<QualityTests> findByOverallStatus(QualityTestStatus overallStatus);
    
    List<QualityTests> findByShipmentIdAndOverallStatus(Long shipmentId, QualityTestStatus overallStatus);
}