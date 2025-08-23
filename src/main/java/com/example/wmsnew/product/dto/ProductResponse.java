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
  private Long categoryId;
  private String categoryName;
  private String createdAt;
  private List<ProductSizeResponse> sizes;
  
  @Data
  public static class ProductSizeResponse {
    private Long standardSizeId;
    private String standardSizeName;
    private Integer maxQuantity;
  }
}
