package com.example.wmsnew.Exceptions.productExceptions;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String format) {
    super(format);
  }
}