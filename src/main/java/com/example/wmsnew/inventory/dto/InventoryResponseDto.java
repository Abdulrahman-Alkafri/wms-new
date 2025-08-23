package com.example.wmsnew.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDto {
    
    private Long id;
    private Long productId;
    private String productName;
    private String brandName;
    private String categoryName;
    private Long locationId;
    private String locationCode;
    private String warehouseName;
    private Integer quantity;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}