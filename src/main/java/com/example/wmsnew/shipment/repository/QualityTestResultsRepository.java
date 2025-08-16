package com.example.wmsnew.shipment.repository;

import com.example.wmsnew.common.enums.TestResultStatus;
import com.example.wmsnew.shipment.entity.QualityTestResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityTestResultsRepository extends JpaRepository<QualityTestResults, Long> {
    
    List<QualityTestResults> findByTestId(Long testId);
    
    List<QualityTestResults> findByShipmentItemId(Long shipmentItemId);
    
    List<QualityTestResults> findByTestStatus(TestResultStatus testStatus);
    
    List<QualityTestResults> findByTestIdAndTestStatus(Long testId, TestResultStatus testStatus);
}