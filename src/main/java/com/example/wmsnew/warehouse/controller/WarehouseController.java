package com.example.wmsnew.warehouse.controller;

import com.example.wmsnew.warehouse.dto.*;
import com.example.wmsnew.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

  private final WarehouseService warehouseService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<WarehouseResponse> createWarehouse(@RequestBody WarehouseRequest request) {
    WarehouseResponse response = warehouseService.createWarehouse(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<Page<AllWarehouseResponse>> getAllWarehouses(
      WarehouseSearchCriteria criteria) {
    return new ResponseEntity<>(warehouseService.findAllWarehouses(criteria), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
    WarehouseResponse response = warehouseService.getWarehouseById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/details")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<DetailedWarehouseResponse> getWarehouseDetails(@PathVariable Long id) {
    DetailedWarehouseResponse response = warehouseService.getWarehouseDetails(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<WarehouseResponse> updateWarehouse(
      @PathVariable Long id, @RequestBody UpdateWarehouseRequest request) {
    WarehouseResponse response = warehouseService.updateWarehouse(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
    warehouseService.deleteWarehouse(id);
    return ResponseEntity.noContent().build();
  }
}
