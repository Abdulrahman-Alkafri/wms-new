package com.example.wmsnew.Exceptions.productExceptions;

public class DuplicateProductException extends RuntimeException {
  public DuplicateProductException(String productName) {
    super("Product with name '" + productName + "' already exists");
  }
}
