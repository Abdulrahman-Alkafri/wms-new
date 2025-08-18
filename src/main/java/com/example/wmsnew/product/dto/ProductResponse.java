package com.example.wmsnew.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
  private Integer id;
  private String productName;
  private String brandName;
  private String description;
  private BigDecimal price;
  private String categoryName;
}
