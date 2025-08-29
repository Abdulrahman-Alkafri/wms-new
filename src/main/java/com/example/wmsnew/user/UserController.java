package com.example.wmsnew.user;

import com.auth0.exception.Auth0Exception;
import com.example.wmsnew.user.dtos.CreateUserDto;
import com.example.wmsnew.user.dtos.UpdateUserDto;
import com.example.wmsnew.user.dtos.UserBaseSearchCriteria;
import com.example.wmsnew.user.dtos.UserResponseDto;
import com.example.wmsnew.user.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> createUser(@RequestBody CreateUserDto createUserDto)
      throws Auth0Exception {
    userService.createUser(createUserDto);
    return ResponseEntity.ok("success");
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ResponseEntity<Page<UserResponseDto>> findAllUsers(
      @ModelAttribute UserBaseSearchCriteria criteria) {
    Page<UserResponseDto> userResponseDtos = userService.findAllUsers(criteria);
    return new ResponseEntity<>(userResponseDtos, HttpStatus.OK);
  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> updateUser(
      @PathVariable Long userId, @Valid @RequestBody UpdateUserDto dto) throws Auth0Exception {

    userService.updateUser(userId, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) throws Auth0Exception {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
