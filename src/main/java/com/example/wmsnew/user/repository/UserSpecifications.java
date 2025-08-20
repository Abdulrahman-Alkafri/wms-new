package com.example.wmsnew.user.repository;

import com.example.wmsnew.common.enums.UserRole;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.warehouse.entity.Warehouse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

  public static Specification<User> searchUser(
      String firstName, String lastName, String email, String phoneNumber, UserRole role, Boolean active) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (firstName != null && !firstName.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("firstName")), 
            "%" + firstName.toLowerCase() + "%"));
      }

      if (lastName != null && !lastName.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("lastName")), 
            "%" + lastName.toLowerCase() + "%"));
      }

      if (email != null && !email.isEmpty()) {
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("email")), 
            "%" + email.toLowerCase() + "%"));
      }

      if (phoneNumber != null && !phoneNumber.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
      }

      if (role != null) {
        predicates.add(criteriaBuilder.equal(root.get("role"), role));
      }

      if (active != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), active));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }

  public static Specification<User> globalSearch(String searchTerm) {
    return (root, query, criteriaBuilder) -> {
      if (searchTerm == null || searchTerm.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      String likePattern = "%" + searchTerm.toLowerCase() + "%";
      
      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
          criteriaBuilder.like(root.get("phoneNumber"), likePattern)
      );
    };
  }
}
