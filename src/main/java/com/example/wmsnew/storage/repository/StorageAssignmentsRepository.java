package com.example.wmsnew.storage.repository;

import com.example.wmsnew.common.enums.AssignmentStatus;
import com.example.wmsnew.storage.entity.StorageAssignments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageAssignmentsRepository extends JpaRepository<StorageAssignments, Long> {
    
    List<StorageAssignments> findByShipmentId(Long shipmentId);
    
    List<StorageAssignments> findByAssignedToId(Long assignedToId);
    
    List<StorageAssignments> findByAssignedById(Long assignedById);
    
    List<StorageAssignments> findByStatus(AssignmentStatus status);
    
    List<StorageAssignments> findByAssignedToIdAndStatus(Long assignedToId, AssignmentStatus status);
}