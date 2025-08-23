package com.example.wmsnew.product.dto;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategorySearchCriteria extends BaseSearchCriteria {
    
    private String name;
    private String globalFilter;
}