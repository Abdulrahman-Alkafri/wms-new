package com.example.wmsnew.supplier;

import com.example.wmsnew.supplier.dto.CreateSupplierDto;
import com.example.wmsnew.supplier.dto.SupplierBaseSearchCriteria;
import com.example.wmsnew.supplier.dto.SupplierResponseDto;
import com.example.wmsnew.supplier.dto.UpdateSupplierDto;
import com.example.wmsnew.supplier.entity.Supplier;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {

  private final SupplierService supplierService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SupplierResponseDto> createSupplier(@RequestBody CreateSupplierDto dto) {
    SupplierResponseDto response = supplierService.createSupplier(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<Page<SupplierResponseDto>> getAllSuppliers(
      @ModelAttribute SupplierBaseSearchCriteria criteria) {
    log.info("=== SupplierController.getAllSuppliers ENTRY ===");
    log.info("Received criteria: {}", criteria);
    try {
      // Ensure we have default values if criteria is empty
      if (criteria == null) {
        criteria = SupplierBaseSearchCriteria.builder().build();
      }
      Page<SupplierResponseDto> suppliers = supplierService.findAllSuppliers(criteria);
      log.info("Successfully retrieved {} suppliers", suppliers.getTotalElements());
      return new ResponseEntity<>(suppliers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error in getAllSuppliers", e);
      throw e;
    }
  }

  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<Page<SupplierResponseDto>> findAllSuppliers(
      @ModelAttribute SupplierBaseSearchCriteria criteria) {
    Page<SupplierResponseDto> suppliers = supplierService.findAllSuppliers(criteria);
    return new ResponseEntity<>(suppliers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
    SupplierResponseDto supplier = supplierService.getSupplierById(id);
    return ResponseEntity.ok(supplier);
  }

  // 🔹 Update Supplier
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Supplier> updateSupplier(
      @PathVariable Long id, @Valid @RequestBody UpdateSupplierDto dto) {

    Supplier updated = supplierService.updateSupplier(id, dto);
    return ResponseEntity.ok(updated);
  }

  // 🔹 Delete Supplier
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build();
  }
}
