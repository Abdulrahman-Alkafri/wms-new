package com.example.wmsnew.Exceptions.userExceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("User with id " + id + " not found");
  }
}
