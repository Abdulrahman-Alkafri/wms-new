package com.example.wmsnew.supplier.repository;

import com.example.wmsnew.supplier.entity.Supplier;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SupplierSpecifications {

  public static Specification<Supplier> searchSupplier(
      String name,
      String contactPerson,
      String email,
      String phoneNumber,
      String city,
      String state) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (name != null && !name.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
      }
      if (contactPerson != null && !contactPerson.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("contactPerson"), "%" + contactPerson + "%"));
      }
      if (email != null && !email.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
      }
      if (phoneNumber != null && !phoneNumber.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
      }
      if (city != null && !city.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("city"), "%" + city + "%"));
      }
      if (state != null && !state.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("state"), "%" + state + "%"));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
