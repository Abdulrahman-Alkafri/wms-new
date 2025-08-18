package com.example.wmsnew.Exceptions.inventoryException;

public class InventoryNotFoundException extends RuntimeException {
  public InventoryNotFoundException(Integer id) {
    super("Inventory record with id " + id + " not found");
  }
}
