package com.example.wmsnew.shipment.dto;

import com.example.wmsnew.common.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDto {
    
    private Long id;
    private String shipmentNumber;
    private ShipmentStatus status;
    private Long supplierId;
    private String supplierName;
    private Long storerId;
    private String storerName;
    private Integer totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ShipmentItemResponseDto> items;
}