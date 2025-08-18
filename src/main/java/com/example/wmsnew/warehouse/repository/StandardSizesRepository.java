package com.example.wmsnew.warehouse.repository;

import com.example.wmsnew.warehouse.entity.StandardSizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardSizesRepository extends JpaRepository<StandardSizes, Long> {
    
    List<StandardSizes> findByWidthAndHeightAndDepth(Integer width, Integer height, Integer depth);
    
    List<StandardSizes> findByMaxWeightGreaterThanEqual(Integer maxWeight);
}