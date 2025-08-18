package com.example.wmsnew.warehouse.dto;

import lombok.Data;

@Data
public class LocationSearchCriteria {
  private String locationCode;
  private String aisle;
  private String rack;
  private String shelf;
  private String bin;
  private String warehouseName; // join filter
  private String standardSizeName; // join filter

  private int page = 0;
  private int size = 10;
  private String sortBy = "id";
  private String sortDir = "ASC";
}
