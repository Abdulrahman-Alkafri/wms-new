package com.example.wmsnew.Exceptions.productExceptions;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(Integer id) {
    super("Category with id " + id + " not found");
  }
}
