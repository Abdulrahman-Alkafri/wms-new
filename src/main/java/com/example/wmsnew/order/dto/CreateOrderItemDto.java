package com.example.wmsnew.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemDto {
    private Integer productId;
    private Integer requestedQuantity;
    private BigDecimal unitPrice;
    private List<InventoryAllocationDto> inventoryAllocations;
}