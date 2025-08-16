package com.example.wmsnew.storage.repository;

import com.example.wmsnew.common.enums.TaskStatus;
import com.example.wmsnew.storage.entity.StorageTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageTasksRepository extends JpaRepository<StorageTasks, Long> {
    
    List<StorageTasks> findByAssignmentId(Long assignmentId);
    
    List<StorageTasks> findByShipmentItemId(Long shipmentItemId);
    
    List<StorageTasks> findByTargetLocationId(Long targetLocationId);
    
    List<StorageTasks> findByStatus(TaskStatus status);
    
    List<StorageTasks> findByAssignmentIdAndStatus(Long assignmentId, TaskStatus status);
}