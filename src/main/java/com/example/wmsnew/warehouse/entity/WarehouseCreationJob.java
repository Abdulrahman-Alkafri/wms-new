package com.example.wmsnew.warehouse.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_creation_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class WarehouseCreationJob extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "warehouse_id")
    private Long warehouseId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    private JobStatus jobStatus;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "total_locations")
    private Integer totalLocations;
    
    @Column(name = "created_locations")
    private Integer createdLocations;
    
    @Column(name = "warehouse_name")
    private String warehouseName;
    
    public enum JobStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    public double getProgressPercentage() {
        if (totalLocations == null || totalLocations == 0) {
            return 0.0;
        }
        return ((double) (createdLocations != null ? createdLocations : 0) / totalLocations) * 100.0;
    }
}