package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class LocationSpecification {

  public static Specification<Location> filterByStandardSize(
      Integer width, Integer height, Integer depth, Integer volume) {
    return (root, query, cb) -> {
      Join<Location, StandardSizes> sizeJoin = root.join("standardSize", JoinType.LEFT);
      Predicate predicate = cb.conjunction();

      if (width != null) predicate = cb.and(predicate, cb.equal(sizeJoin.get("width"), width));
      if (height != null) predicate = cb.and(predicate, cb.equal(sizeJoin.get("height"), height));
      if (depth != null) predicate = cb.and(predicate, cb.equal(sizeJoin.get("depth"), depth));
      if (volume != null) predicate = cb.and(predicate, cb.equal(sizeJoin.get("volume"), volume));

      return predicate;
    };
  }

  public static Specification<Location> belongsToWarehouse(Integer warehouseId) {
    return (root, query, cb) -> {
      if (warehouseId == null) return cb.conjunction();
      return cb.equal(root.get("warehouse").get("id"), warehouseId);
    };
  }
}
