package com.example.wmsnew.product.repository;

import com.example.wmsnew.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.productStandardSizes pss " +
           "LEFT JOIN FETCH pss.standardSizes " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Integer id);
}
