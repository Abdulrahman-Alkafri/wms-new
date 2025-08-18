package com.example.wmsnew.product.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.Data;

@Data
public class ProductBaseSearchCriteria extends BaseSearchCriteria {
  private String productName;
  private String brandName;
  private String categoryName;
}
