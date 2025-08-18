package com.example.wmsnew.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDto {
  private Long id;
  private String name;
  private String contactPerson;
  private String email;
  private String phoneNumber;
  private String address;
  private String city;
  private String state;
}
