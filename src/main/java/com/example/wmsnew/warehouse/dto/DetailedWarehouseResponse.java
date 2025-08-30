package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailedWarehouseResponse {
    private Long id;
    private String warehouseName;
    private Integer totalLocations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LocationGroupSummary> locationGroups;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationGroupSummary {
        private Long standardSizeId;
        private String standardSizeName;
        private Integer locationCount;
        private String description;
    }
}
