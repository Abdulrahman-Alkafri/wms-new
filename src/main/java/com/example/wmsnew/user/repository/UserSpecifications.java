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
      String firstName, String lastName, String email, String phoneNumber, UserRole role) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (firstName != null && !firstName.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("name"), "%" + firstName + "%"));
      }

      if (lastName != null && !lastName.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("name"), "%" + lastName + "%"));
      }

      if (email != null && !email.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
      }

      if (phoneNumber != null && !phoneNumber.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
      }

      if (role != null) {
        predicates.add(criteriaBuilder.equal(root.get("role"), role));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }
}
