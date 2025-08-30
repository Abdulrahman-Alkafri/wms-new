package com.example.wmsnew.supplier.dto;


import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierBaseSearchCriteria extends BaseSearchCriteria {
    private String name;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String globalSearch;
}
