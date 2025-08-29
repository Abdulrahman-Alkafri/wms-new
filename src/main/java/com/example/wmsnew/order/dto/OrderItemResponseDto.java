package com.example.wmsnew.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer requestedQuantity;
    private Integer pickedQuantity;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private BigDecimal unitPrice;
}