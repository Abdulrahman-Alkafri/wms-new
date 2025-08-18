package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Warehouse;
import org.springframework.data.jpa.domain.Specification;

public class WarehouseSpecifications {

  public static Specification<Warehouse> searchWarehouse(String warehouseName) {
    return (root, query, criteriaBuilder) -> {
      if (warehouseName != null && !warehouseName.isEmpty()) {
        return criteriaBuilder.like(
            criteriaBuilder.lower(root.get("warehouseName")),
            "%" + warehouseName.toLowerCase() + "%");
      }
      return criteriaBuilder.conjunction();
    };
  }
}
