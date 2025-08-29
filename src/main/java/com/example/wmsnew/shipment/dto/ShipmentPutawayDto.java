package com.example.wmsnew.shipment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentPutawayDto {
    private Integer id;
    private Integer shipmentItemId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private String batchNumber;
    private Long locationId;
    private String locationCode;
    private String locationDetails; // Aisle, Rack, Shelf, Bin details
    private Integer putawayQuantity;
}
