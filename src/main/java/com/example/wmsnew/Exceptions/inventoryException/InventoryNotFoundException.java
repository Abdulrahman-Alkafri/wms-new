package com.example.wmsnew.Exceptions.inventoryException;

public class InventoryNotFoundException extends RuntimeException {
  public InventoryNotFoundException(Long id) {
    super("Inventory record with id " + id + " not found");
  }
  
  public InventoryNotFoundException(String message) {
    super(message);
  }
}
