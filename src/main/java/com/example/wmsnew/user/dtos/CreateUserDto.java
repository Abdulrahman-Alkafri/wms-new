package com.example.wmsnew.user.dtos;

import com.example.wmsnew.common.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name cannot exceed 100 characters")
  private String name;

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name cannot exceed 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name cannot exceed 50 characters")
  private String lastName;

  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email cannot exceed 100 characters")
  private String email;

  @Pattern(regexp = "^\\+?[0-9.\\-()\\s]{7,15}$", message = "Phone number is invalid")
  private String phoneNumber;

  @NotNull(message = "Role is required")
  private UserRole role;

}
