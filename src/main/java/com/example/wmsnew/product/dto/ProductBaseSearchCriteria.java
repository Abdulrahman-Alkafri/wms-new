package com.example.wmsnew.product.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseSearchCriteria extends BaseSearchCriteria {
  private String productName;
  private String brandName;
  private String categoryName;
}
