package com.example.wmsnew.customer.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerDto {
  @NotBlank(message = "Name is required")
  private String name;

  @Email(message = "Email should be valid")
  private String email;

  private String phoneNumber;

  private String address;

  private String city;

  private String state;
}
