package com.example.wmsnew.warehouse.dto;

import com.example.wmsnew.warehouse.entity.WarehouseCreationJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseCreationStatusResponse {
    private Long jobId;
    private Long warehouseId;
    private String warehouseName;
    private WarehouseCreationJob.JobStatus jobStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private Integer totalLocations;
    private Integer createdLocations;
    private Double progressPercentage;
    private Long durationSeconds;
    
    public static WarehouseCreationStatusResponse fromEntity(WarehouseCreationJob job) {
        return WarehouseCreationStatusResponse.builder()
                .jobId(job.getId())
                .warehouseId(job.getWarehouseId())
                .warehouseName(job.getWarehouseName())
                .jobStatus(job.getJobStatus())
                .startTime(job.getStartTime())
                .endTime(job.getEndTime())
                .errorMessage(job.getErrorMessage())
                .totalLocations(job.getTotalLocations())
                .createdLocations(job.getCreatedLocations())
                .progressPercentage(job.getProgressPercentage())
                .durationSeconds(calculateDuration(job.getStartTime(), job.getEndTime()))
                .build();
    }
    
    private static Long calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null) return null;
        if (end == null) return java.time.Duration.between(start, LocalDateTime.now()).getSeconds();
        return java.time.Duration.between(start, end).getSeconds();
    }
}