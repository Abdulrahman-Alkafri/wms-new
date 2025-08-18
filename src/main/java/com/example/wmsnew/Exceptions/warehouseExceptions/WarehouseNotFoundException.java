package com.example.wmsnew.Exceptions.warehouseExceptions;

public class WarehouseNotFoundException extends RuntimeException {
  public WarehouseNotFoundException(Long id) {
    super("Warehouse with id " + id + " not found");
  }
}
