package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.Location;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LocationSpecifications {

  public static Specification<Location> searchLocation(
      String locationCode,
      String aisle,
      String rack,
      String shelf,
      String bin,
      String warehouseName,
      String standardSizeName) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (locationCode != null && !locationCode.isEmpty()) {
        predicates.add(
            cb.like(cb.lower(root.get("locationCode")), "%" + locationCode.toLowerCase() + "%"));
      }

      if (aisle != null && !aisle.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("asile")), "%" + aisle.toLowerCase() + "%"));
      }

      if (rack != null && !rack.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("rack")), "%" + rack.toLowerCase() + "%"));
      }

      if (shelf != null && !shelf.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("shelf")), "%" + shelf.toLowerCase() + "%"));
      }

      if (bin != null && !bin.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("bin")), "%" + bin.toLowerCase() + "%"));
      }

      if (warehouseName != null && !warehouseName.isEmpty()) {
        predicates.add(
            cb.like(
                cb.lower(root.join("warehouse").get("warehouseName")),
                "%" + warehouseName.toLowerCase() + "%"));
      }

      if (standardSizeName != null && !standardSizeName.isEmpty()) {
        predicates.add(
            cb.like(
                cb.lower(root.join("standardSize").get("width").as(String.class)),
                "%" + standardSizeName.toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
