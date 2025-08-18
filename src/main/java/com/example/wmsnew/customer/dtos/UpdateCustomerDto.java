package com.example.wmsnew.customer.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerDto {

  private String name;

  @Email(message = "Invalid email format")
  private String email;

  private String phoneNumber;

  private String address;

  private String city;

  private String state;
}