package com.example.wmsnew.user.services;

import com.auth0.exception.Auth0Exception;
import com.example.wmsnew.Exceptions.userExceptions.UserNotFoundException;
import com.example.wmsnew.user.dtos.CreateUserDto;
import com.example.wmsnew.user.dtos.UpdateUserDto;
import com.example.wmsnew.user.dtos.UserBaseSearchCriteria;
import com.example.wmsnew.user.dtos.UserResponseDto;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.user.repository.UserRepository;
import com.example.wmsnew.user.repository.UserSpecifications;
import com.example.wmsnew.warehouse.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

  private UserRepository userRepository;
  private Auth0Service auth0Service;
  private WarehouseRepository warehouseRepository;

  public void createUser(@RequestBody CreateUserDto createUserDto) throws Auth0Exception {
    String auth0Id =
        auth0Service.createUser(
            createUserDto.getEmail(),
            createUserDto.getPassword(),
            createUserDto.getRole().toString());
    User user = new User();
    user.setName(createUserDto.getName());
    user.setEmail(createUserDto.getEmail());
    user.setAuth0Id(auth0Id);
    user.setRole(createUserDto.getRole());
    user.setFirstName(createUserDto.getFirstName());
    user.setLastName(createUserDto.getLastName());
    user.setPhoneNumber(createUserDto.getPhoneNumber());
    user.setIsActive(true);
    userRepository.save(user);
  }

  public Page<UserResponseDto> findAllUsers(UserBaseSearchCriteria cs) {
    log.info("UserService.findAllUsers called with criteria: {}", cs);
    
    // Handle null criteria
    if (cs == null) {
      log.info("Creating default criteria");
      cs = UserBaseSearchCriteria.builder().build();
    }
    log.info("Criteria after null check: {}", cs);
    
    try {
      Specification<User> userSpecification;
      
      if (cs.getGlobalSearch() != null && !cs.getGlobalSearch().isEmpty()) {
        log.info("Using global search: {}", cs.getGlobalSearch());
        userSpecification = UserSpecifications.globalSearch(cs.getGlobalSearch());
        
        // Apply additional filters even with global search
        if (cs.getRole() != null || cs.getActive() != null) {
          Specification<User> filterSpec = UserSpecifications.searchUser(
              null, null, null, null, cs.getRole(), cs.getActive());
          userSpecification = userSpecification.and(filterSpec);
        }
      } else {
        log.info("Using detailed search");
        userSpecification = UserSpecifications.searchUser(
            cs.getFirstName(), cs.getLastName(), cs.getEmail(), cs.getPhoneNumber(), cs.getRole(), cs.getActive());
      }

      // Handle null values with defaults
      Integer page = cs.getPage() != null ? cs.getPage() : 1;
      Integer size = cs.getSize() != null ? cs.getSize() : 10;
      String sortBy = cs.getSortBy() != null ? cs.getSortBy() : "id";
      String sortDir = cs.getSortDir() != null ? cs.getSortDir() : "asc";
      
      log.info("Pagination: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

      Pageable pageable =
          PageRequest.of(
              page - 1, // Convert to 0-based indexing
              size,
              Sort.by(Sort.Direction.fromString(sortDir), sortBy));

      log.info("About to call userRepository.findAll...");
      Page<User> usersPage = userRepository.findAll(userSpecification, pageable);
      log.info("Repository call completed. Found {} users", usersPage.getTotalElements());

      return usersPage.map(
          user ->
              UserResponseDto.builder()
                  .id(user.getId())
                  .firstName(user.getFirstName())
                  .lastName(user.getLastName())
                  .email(user.getEmail())
                  .phoneNumber(user.getPhoneNumber())
                  .role(user.getRole())
                  .isActive(user.getIsActive())
                  .createdAt(user.getCreatedAt().toString())
                  .build());
    } catch (Exception e) {
      log.error("Error in findAllUsers", e);
      throw e;
    }
  }

  public void updateUser(Long userId, UpdateUserDto dto) throws Auth0Exception {

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    if (dto.getName() != null) {
      user.setName(dto.getName());
    }
    if (dto.getFirstName() != null) {
      user.setFirstName(dto.getFirstName());
    }
    if (dto.getLastName() != null) {
      user.setLastName(dto.getLastName());
    }
    if (dto.getPhoneNumber() != null) {
      user.setPhoneNumber(dto.getPhoneNumber());
    }

    if (dto.getRole() != null) {
      auth0Service.updateUserRole(user.getAuth0Id(), dto.getRole().name());
      user.setRole(dto.getRole());
    }

    userRepository.save(user);
  }

  public void deleteUser(Long userId) throws Auth0Exception {

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    auth0Service.deleteUser(user.getAuth0Id());

    userRepository.deleteById(userId);
  }
}
