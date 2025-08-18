package com.example.wmsnew.Exceptions.warehouseExceptions;

public class LocationNotFoundException extends RuntimeException {
  public LocationNotFoundException(Integer id) {
    super("Location with id " + id + " not found");
  }
}
