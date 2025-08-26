package com.example.wmsnew.Exceptions.shipmentExceptions;

public class ShipmentNotFoundException extends RuntimeException {
  public ShipmentNotFoundException(String format) {
    super(format);
  }
}
