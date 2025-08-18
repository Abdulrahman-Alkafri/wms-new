// Product creation request
package com.example.wmsnew.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreateRequest {
  private String productName;
  private String brandName;
  private String description;
  private BigDecimal price;
  private Long categoryId;
  private List<ProductSizeDto> sizes;
}
