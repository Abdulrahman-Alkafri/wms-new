package com.example.wmsnew.Exceptions.productExceptions;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(Integer id) {
    super("Product with id " + id + " not found");
  }
}