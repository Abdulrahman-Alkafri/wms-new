package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.WarehouseCreationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseCreationJobRepository extends JpaRepository<WarehouseCreationJob, Long> {
    
    Optional<WarehouseCreationJob> findByWarehouseId(Long warehouseId);
    
    List<WarehouseCreationJob> findByJobStatusOrderByStartTimeDesc(WarehouseCreationJob.JobStatus jobStatus);
    
    List<WarehouseCreationJob> findAllByOrderByStartTimeDesc();
}