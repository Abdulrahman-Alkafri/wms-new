package com.example.wmsnew.shipment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateShipmentDto {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    @NotBlank(message = "Shipment number is required")
    private String shipmentNumber;
    
    @NotEmpty(message = "Shipment items are required")
    @Valid
    private List<CreateShipmentItemDto> items;
}