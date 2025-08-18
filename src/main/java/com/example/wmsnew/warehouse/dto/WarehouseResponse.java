package com.example.wmsnew.warehouse.dto;

// WarehouseResponse.java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    private Integer id;
    private String warehouseName;
    private List<String> locationCodes; // List of all generated locations
}
