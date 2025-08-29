package com.example.wmsnew.order.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAllocationDto {
    private Long inventoryId;
    private Integer quantityToAllocate;
}