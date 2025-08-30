package com.example.wmsnew.warehouse.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WarehouseSearchCriteria extends BaseSearchCriteria {
  private String warehouseName;
}
