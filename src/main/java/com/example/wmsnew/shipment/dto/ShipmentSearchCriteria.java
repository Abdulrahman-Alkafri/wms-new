package com.example.wmsnew.shipment.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import com.example.wmsnew.common.enums.ShipmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShipmentSearchCriteria extends BaseSearchCriteria {
    
    private String shipmentNumber;
    private ShipmentStatus status;
    private Integer supplierId;
    private String supplierName;
    private Integer storerId;
    private String storerName;
}