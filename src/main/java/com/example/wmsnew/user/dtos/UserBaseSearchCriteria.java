package com.example.wmsnew.user.dtos;

import com.example.wmsnew.common.dtos.BaseSearchCriteria;
import com.example.wmsnew.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserBaseSearchCriteria extends BaseSearchCriteria {
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private Boolean active;
  private UserRole role;
  private String globalSearch;
}
