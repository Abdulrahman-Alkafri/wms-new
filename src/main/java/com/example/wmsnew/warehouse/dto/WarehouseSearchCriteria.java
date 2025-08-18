package com.example.wmsnew.warehouse.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.Data;

@Data
public class WarehouseSearchCriteria extends BaseSearchCriteria {
  private String warehouseName;
}
