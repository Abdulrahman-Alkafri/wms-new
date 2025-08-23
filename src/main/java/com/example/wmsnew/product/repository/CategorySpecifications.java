package com.example.wmsnew.product.repository;

import com.example.wmsnew.product.dto.CategorySearchCriteria;
import com.example.wmsnew.product.entity.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecifications {

    public static Specification<Category> withCriteria(CategorySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name (case-insensitive partial match)
            if (criteria.getName() != null && !criteria.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + criteria.getName().toLowerCase() + "%"
                ));
            }

            // Global search across all text fields
            if (criteria.getGlobalFilter() != null && !criteria.getGlobalFilter().trim().isEmpty()) {
                String globalFilter = "%" + criteria.getGlobalFilter().toLowerCase() + "%";
                
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    globalFilter
                );
                
                predicates.add(namePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}