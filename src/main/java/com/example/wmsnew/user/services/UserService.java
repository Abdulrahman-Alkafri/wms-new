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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
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
    user.setEmail(createUserDto.getEmail());
    user.setAuth0Id(auth0Id);
    user.setRole(createUserDto.getRole());
    user.setFirstName(createUserDto.getFirstName());
    user.setLastName(createUserDto.getLastName());
    user.setPhoneNumber(createUserDto.getPhoneNumber());
    userRepository.save(user);
  }

  public Page<UserResponseDto> findAllUsers(UserBaseSearchCriteria cs) {
    Specification<User> userSpecification =
        UserSpecifications.searchUser(
            cs.getFirstName(), cs.getLastName(), cs.getEmail(), cs.getPhoneNumber(), cs.getRole());

    Pageable pageable =
        PageRequest.of(
            cs.getPage(),
            cs.getSize(),
            Sort.by(Sort.Direction.fromString(cs.getSortDir()), cs.getSortBy()));

    Page<User> usersPage = userRepository.findAll(userSpecification, pageable);

    return usersPage.map(
        user ->
            UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build());
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
