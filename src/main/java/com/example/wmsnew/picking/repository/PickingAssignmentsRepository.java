package com.example.wmsnew.picking.repository;

import com.example.wmsnew.common.enums.AssignmentStatus;
import com.example.wmsnew.picking.entity.PickingAssignments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickingAssignmentsRepository extends JpaRepository<PickingAssignments, Long> {
    
    List<PickingAssignments> findByOrderId(Long orderId);
    
    List<PickingAssignments> findByAssignedToId(Long assignedToId);
    
    List<PickingAssignments> findByAssignedById(Long assignedById);
    
    List<PickingAssignments> findByStatus(AssignmentStatus status);
    
    List<PickingAssignments> findByAssignedToIdAndStatus(Long assignedToId, AssignmentStatus status);
}