package com.example.wmsnew.picking.repository;

import com.example.wmsnew.common.enums.TaskStatus;
import com.example.wmsnew.picking.entity.PickingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickingDetailsRepository extends JpaRepository<PickingDetails, Long> {
    
    List<PickingDetails> findByAssignmentId(Long assignmentId);
    
    List<PickingDetails> findByOrderItemId(Long orderItemId);
    
    List<PickingDetails> findByInventoryId(Long inventoryId);
    
    List<PickingDetails> findByStatus(TaskStatus status);
    
    List<PickingDetails> findByAssignmentIdAndStatus(Long assignmentId, TaskStatus status);
}