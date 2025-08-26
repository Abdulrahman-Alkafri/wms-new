package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository
    extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    
    @Query("SELECT l FROM Location l WHERE l.standardSize = :standardSize " +
           "AND (l.standardSize.volume - l.currentLoad) >= :requiredCapacity " +
           "ORDER BY l.currentLoad ASC")
    List<Location> findByStandardSizeAndAvailableCapacity(
            @Param("standardSize") StandardSizes standardSize, 
            @Param("requiredCapacity") Double requiredCapacity
    );
}
