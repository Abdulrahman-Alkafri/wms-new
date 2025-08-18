package com.example.wmsnew.user.dtos;

import com.example.wmsnew.common.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserDto {

  private String name;

  private String firstName;

  private String lastName;

  private String phoneNumber;

  private UserRole role;
}
