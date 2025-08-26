package com.example.wmsnew.user.dtos;

import com.example.wmsnew.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private UserRole role;
  private Boolean isActive;
  private String createdAt;
}
