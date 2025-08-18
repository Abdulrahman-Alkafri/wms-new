package com.example.wmsnew.warehouse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponseDto {
  private Integer id;
  private String locationCode;
  private String aisle;
  private String rack;
  private String shelf;
  private String bin;
  private Integer standardSizeId;
  private String standardSizeName;
  private String warehouseName;
  private Double currentLoad; // default 0.0
}
