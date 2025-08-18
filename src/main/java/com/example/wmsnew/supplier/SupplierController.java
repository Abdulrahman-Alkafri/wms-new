package com.example.wmsnew.supplier;

import com.example.wmsnew.supplier.dto.CreateSupplierDto;
import com.example.wmsnew.supplier.dto.SupplierBaseSearchCriteria;
import com.example.wmsnew.supplier.dto.SupplierResponseDto;
import com.example.wmsnew.supplier.dto.UpdateSupplierDto;
import com.example.wmsnew.supplier.entity.Supplier;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;

  @PostMapping
  public ResponseEntity<SupplierResponseDto> createSupplier(@RequestBody CreateSupplierDto dto) {
    SupplierResponseDto response = supplierService.createSupplier(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<SupplierResponseDto>> findAllSuppliers(
      @ModelAttribute SupplierBaseSearchCriteria criteria) {
    Page<SupplierResponseDto> suppliers = supplierService.findAllSuppliers(criteria);
    return new ResponseEntity<>(suppliers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
    SupplierResponseDto supplier = supplierService.getSupplierById(id);
    return ResponseEntity.ok(supplier);
  }

  // 🔹 Update Supplier
  @PutMapping("/{id}")
  public ResponseEntity<Supplier> updateSupplier(
      @PathVariable Long id, @Valid @RequestBody UpdateSupplierDto dto) {

    Supplier updated = supplierService.updateSupplier(id, dto);
    return ResponseEntity.ok(updated);
  }

  // 🔹 Delete Supplier
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build();
  }
}
