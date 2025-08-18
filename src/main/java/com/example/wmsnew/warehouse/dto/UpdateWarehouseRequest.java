package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWarehouseRequest {
  private String warehouseName; // optional
  private List<LocationGroupRequest> locationGroups; // optional
}
