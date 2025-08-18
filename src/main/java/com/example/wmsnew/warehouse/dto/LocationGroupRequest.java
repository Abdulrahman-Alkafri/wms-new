package com.example.wmsnew.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationGroupRequest {
  private Long standardSizeId;
  private int rows;
  private int racksPerRow;
  private int shelvesPerRack;
  private int binsPerShelf;
}
