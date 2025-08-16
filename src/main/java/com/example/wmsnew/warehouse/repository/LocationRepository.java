package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    List<Location> findByZoneId(Long zoneId);
    
    List<Location> findByIsActiveTrue();
    
    List<Location> findByZoneIdAndIsActiveTrue(Long zoneId);
    
    List<Location> findByStandardSizesId(Long standardSizesId);
    
    List<Location> findByLocationCode(String locationCode);
}