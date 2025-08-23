package com.example.wmsnew.inventory.repository;

import com.example.wmsnew.inventory.dto.InventorySearchCriteria;
import com.example.wmsnew.inventory.entity.Inventory;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InventorySpecifications {

    public static Specification<Inventory> withCriteria(InventorySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join tables for related entities
            Join<Object, Object> productJoin = root.join("product", JoinType.LEFT);
            Join<Object, Object> categoryJoin = productJoin.join("category", JoinType.LEFT);
            Join<Object, Object> locationJoin = root.join("location", JoinType.LEFT);
            Join<Object, Object> warehouseJoin = locationJoin.join("warehouse", JoinType.LEFT);

            // Filter by product name
            if (criteria.getProductName() != null && !criteria.getProductName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("productName")),
                    "%" + criteria.getProductName().toLowerCase() + "%"
                ));
            }

            // Filter by brand name
            if (criteria.getBrandName() != null && !criteria.getBrandName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("brandName")),
                    "%" + criteria.getBrandName().toLowerCase() + "%"
                ));
            }

            // Filter by category name
            if (criteria.getCategoryName() != null && !criteria.getCategoryName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(categoryJoin.get("name")),
                    "%" + criteria.getCategoryName().toLowerCase() + "%"
                ));
            }

            // Filter by location code
            if (criteria.getLocationCode() != null && !criteria.getLocationCode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(locationJoin.get("locationCode")),
                    "%" + criteria.getLocationCode().toLowerCase() + "%"
                ));
            }

            // Filter by warehouse name
            if (criteria.getWarehouseName() != null && !criteria.getWarehouseName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(warehouseJoin.get("warehouseName")),
                    "%" + criteria.getWarehouseName().toLowerCase() + "%"
                ));
            }

            // Filter by batch number
            if (criteria.getBatchNumber() != null && !criteria.getBatchNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("batchNumber")),
                    "%" + criteria.getBatchNumber().toLowerCase() + "%"
                ));
            }

            // Filter by quantity range
            if (criteria.getMinQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("quantity"), criteria.getMinQuantity()
                ));
            }
            if (criteria.getMaxQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("quantity"), criteria.getMaxQuantity()
                ));
            }

            // Filter by manufacturing date range
            if (criteria.getManufacturingDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("manufacturingDate"), criteria.getManufacturingDateFrom()
                ));
            }
            if (criteria.getManufacturingDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("manufacturingDate"), criteria.getManufacturingDateTo()
                ));
            }

            // Filter by expiry date range
            if (criteria.getExpiryDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("expiryDate"), criteria.getExpiryDateFrom()
                ));
            }
            if (criteria.getExpiryDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("expiryDate"), criteria.getExpiryDateTo()
                ));
            }

            // Filter by has expiry date
            if (criteria.getHasExpiry() != null) {
                if (criteria.getHasExpiry()) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("expiryDate")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("expiryDate")));
                }
            }

            // Global search across multiple text fields
            if (criteria.getGlobalFilter() != null && !criteria.getGlobalFilter().trim().isEmpty()) {
                String globalFilter = "%" + criteria.getGlobalFilter().toLowerCase() + "%";
                
                Predicate productNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("productName")), 
                    globalFilter
                );
                
                Predicate brandNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(productJoin.get("brandName")), 
                    globalFilter
                );
                
                Predicate categoryNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(categoryJoin.get("name")), 
                    globalFilter
                );
                
                Predicate locationCodePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(locationJoin.get("locationCode")), 
                    globalFilter
                );
                
                Predicate warehouseNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(warehouseJoin.get("warehouseName")), 
                    globalFilter
                );
                
                Predicate batchNumberPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("batchNumber")), 
                    globalFilter
                );
                
                predicates.add(criteriaBuilder.or(
                    productNamePredicate,
                    brandNamePredicate,
                    categoryNamePredicate,
                    locationCodePredicate,
                    warehouseNamePredicate,
                    batchNumberPredicate
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}