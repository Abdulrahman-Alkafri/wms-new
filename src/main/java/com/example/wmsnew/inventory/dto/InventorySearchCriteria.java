package com.example.wmsnew.inventory.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventorySearchCriteria extends BaseSearchCriteria {
    
    private String productName;
    private String brandName;
    private String categoryName;
    private String locationCode;
    private String warehouseName;
    private String batchNumber;
    private Integer minQuantity;
    private Integer maxQuantity;
    private LocalDate manufacturingDateFrom;
    private LocalDate manufacturingDateTo;
    private LocalDate expiryDateFrom;
    private LocalDate expiryDateTo;
    private Boolean hasExpiry;
    private String globalFilter;
}