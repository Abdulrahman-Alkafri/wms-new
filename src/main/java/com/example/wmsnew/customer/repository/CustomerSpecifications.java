package com.example.wmsnew.customer.repository;

import com.example.wmsnew.customer.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecifications {

    public static Specification<Customer> searchCustomer(
            String name, String email, String phoneNumber, String city, String state) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
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
