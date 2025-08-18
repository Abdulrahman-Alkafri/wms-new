package com.example.wmsnew.product.repository;

import com.example.wmsnew.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

  public static Specification<Product> searchProduct(
      String productName, String brandName, String categoryName) {
    return (root, query, cb) -> {
      var predicates = cb.conjunction();

      if (productName != null && !productName.isEmpty()) {
        predicates =
            cb.and(
                predicates,
                cb.like(cb.lower(root.get("productName")), "%" + productName.toLowerCase() + "%"));
      }

      if (brandName != null && !brandName.isEmpty()) {
        predicates =
            cb.and(predicates, cb.equal(cb.lower(root.get("brandName")), brandName.toLowerCase()));
      }

      if (categoryName != null && !categoryName.isEmpty()) {
        predicates =
            cb.and(
                predicates,
                cb.equal(cb.lower(root.join("category").get("name")), categoryName.toLowerCase()));
      }

      return predicates;
    };
  }
}
