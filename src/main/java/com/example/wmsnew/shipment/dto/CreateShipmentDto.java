package com.example.wmsnew.shipment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateShipmentDto {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    // Shipment number is now optional - will be auto-generated if not provided
    private String shipmentNumber;
    
    @NotEmpty(message = "Shipment items are required")
    @Valid
    private List<CreateShipmentItemDto> items;
}
