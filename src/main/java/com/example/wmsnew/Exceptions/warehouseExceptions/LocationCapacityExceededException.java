package com.example.wmsnew.Exceptions.warehouseExceptions;

public class LocationCapacityExceededException extends RuntimeException {
  public LocationCapacityExceededException(Integer locationId) {
    super("Location with id " + locationId + " has reached its maximum capacity");
  }
}
