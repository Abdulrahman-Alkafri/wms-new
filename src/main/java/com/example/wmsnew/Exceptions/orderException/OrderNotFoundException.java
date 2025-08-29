package com.example.wmsnew.Exceptions.orderException;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(String message) {
    super(message);
  }
  
  public OrderNotFoundException(Integer id) {
    super("Order with id " + id + " not found");
  }
}