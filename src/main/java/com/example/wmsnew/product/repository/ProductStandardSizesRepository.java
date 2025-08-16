package com.example.wmsnew.product.repository;

import com.example.wmsnew.product.entity.ProductStandardSizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStandardSizesRepository extends JpaRepository<ProductStandardSizes, Long> {
    
    List<ProductStandardSizes> findByProductId(Long productId);
    
    List<ProductStandardSizes> findByStandardSizesId(Long standardSizesId);
    
    List<ProductStandardSizes> findByProductIdAndStandardSizesId(Long productId, Long standardSizesId);
}