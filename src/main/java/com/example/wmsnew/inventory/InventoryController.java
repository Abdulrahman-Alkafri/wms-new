package com.example.wmsnew.inventory;

import com.example.wmsnew.inventory.dto.InventoryResponseDto;
import com.example.wmsnew.inventory.dto.InventorySearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<Page<InventoryResponseDto>> searchInventory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String locationCode,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate manufacturingDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate manufacturingDateTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateTo,
            @RequestParam(required = false) Boolean hasExpiry,
            @RequestParam(required = false) String globalFilter) {
        
        InventorySearchCriteria criteria = InventorySearchCriteria.builder()
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .sortDir(sortDir)
            .productName(productName)
            .brandName(brandName)
            .categoryName(categoryName)
            .locationCode(locationCode)
            .warehouseName(warehouseName)
            .batchNumber(batchNumber)
            .minQuantity(minQuantity)
            .maxQuantity(maxQuantity)
            .manufacturingDateFrom(manufacturingDateFrom)
            .manufacturingDateTo(manufacturingDateTo)
            .expiryDateFrom(expiryDateFrom)
            .expiryDateTo(expiryDateTo)
            .hasExpiry(hasExpiry)
            .globalFilter(globalFilter)
            .build();

        Page<InventoryResponseDto> inventory = inventoryService.getAllInventory(criteria);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<InventoryResponseDto> getInventoryById(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/by-product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<List<InventoryResponseDto>> getAvailableInventoryByProduct(@PathVariable Integer productId) {
        List<InventoryResponseDto> inventory = inventoryService.getAvailableInventoryByProduct(productId);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{id}/quantity")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryResponseDto> updateInventoryQuantity(
            @PathVariable Long id, 
            @RequestBody UpdateQuantityRequest request) {
        try {
            if (request.getQuantity() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            InventoryResponseDto updatedInventory = inventoryService.updateInventoryQuantity(id, request.getQuantity());
            return ResponseEntity.ok(updatedInventory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO for updating quantity
    public static class UpdateQuantityRequest {
        private Integer quantity;
        
        public UpdateQuantityRequest() {}
        
        public UpdateQuantityRequest(Integer quantity) {
            this.quantity = quantity;
        }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}