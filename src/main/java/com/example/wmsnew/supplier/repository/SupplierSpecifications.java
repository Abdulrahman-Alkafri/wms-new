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
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")), 
            "%" + name.toLowerCase() + "%"));
      }
      if (contactPerson != null && !contactPerson.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("contactPerson")), 
            "%" + contactPerson.toLowerCase() + "%"));
      }
      if (email != null && !email.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("email")), 
            "%" + email.toLowerCase() + "%"));
      }
      if (phoneNumber != null && !phoneNumber.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
      }
      if (city != null && !city.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("city")), 
            "%" + city.toLowerCase() + "%"));
      }
      if (state != null && !state.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("state")), 
            "%" + state.toLowerCase() + "%"));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Supplier> globalSearch(String searchTerm) {
    return (root, query, criteriaBuilder) -> {
      if (searchTerm == null || searchTerm.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      String likePattern = "%" + searchTerm.toLowerCase() + "%";
      
      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("contactPerson")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
          criteriaBuilder.like(root.get("phoneNumber"), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("state")), likePattern)
      );
    };
  }
}
