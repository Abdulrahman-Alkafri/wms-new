package com.example.wmsnew.supplier.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupplierDto {

  private String name;

  private String contactPerson;

  @Email(message = "Invalid email format")
  private String email;

  private String phoneNumber;

  private String address;

  private String city;

  private String state;
}
