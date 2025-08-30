package com.example.wmsnew.customer.dtos;

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
public class CustomerBaseSearchCriteria extends BaseSearchCriteria {
  private String name;
  private String email;
  private String phoneNumber;
  private String city;
  private String state;
  private String globalSearch;
}
