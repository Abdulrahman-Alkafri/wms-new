package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncWarehouseCreationResponse {
    private Long jobId;
    private Long warehouseId;
    private String message;
    private Integer estimatedTotalLocations;
}