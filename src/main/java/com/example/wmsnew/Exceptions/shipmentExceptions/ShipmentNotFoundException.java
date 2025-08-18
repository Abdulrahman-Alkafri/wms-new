package com.example.wmsnew.Exceptions.shipmentExceptions;

public class ShipmentNotFoundException extends RuntimeException {
  public ShipmentNotFoundException(Integer id) {
    super("Shipment with id " + id + " not found");
  }
}
