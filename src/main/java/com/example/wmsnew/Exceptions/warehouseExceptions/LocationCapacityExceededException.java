package com.example.wmsnew.Exceptions.warehouseExceptions;

public class LocationCapacityExceededException extends RuntimeException {
  public LocationCapacityExceededException(String format) {
    super("Location with id " + format + " has reached its maximum capacity");
  }
}
