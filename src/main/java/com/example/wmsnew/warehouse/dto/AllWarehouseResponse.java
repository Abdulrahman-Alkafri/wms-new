package com.example.wmsnew.warehouse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllWarehouseResponse {
  private Integer id;
  private String warehouseName;
  private int totalLocations;
}
