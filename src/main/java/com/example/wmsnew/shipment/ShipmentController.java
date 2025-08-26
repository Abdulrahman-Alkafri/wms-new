package com.example.wmsnew.shipment;

import com.example.wmsnew.shipment.dto.CreateShipmentDto;
import com.example.wmsnew.shipment.dto.ShipmentResponseDto;
import com.example.wmsnew.shipment.dto.ShipmentSearchCriteria;
import com.example.wmsnew.shipment.dto.UpdateShipmentDto;
import com.example.wmsnew.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class ShipmentController {
    
    private final ShipmentService shipmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDto> createShipment(@Valid @RequestBody CreateShipmentDto createDto) {
        ShipmentResponseDto response = shipmentService.createShipment(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<ShipmentResponseDto>> getShipments(
            ShipmentSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("=== ShipmentController.getShipments ENTRY ===");
        log.info("Received criteria: {}", criteria);
        try {
            // Handle null criteria by creating a default instance
            if (criteria == null) {
                log.info("Creating default criteria");
                criteria = new ShipmentSearchCriteria();
            }
            Page<ShipmentResponseDto> response = shipmentService.getShipments(criteria, pageable);
            log.info("Successfully retrieved {} shipments", response.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getShipments", e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Integer id) {
        ShipmentResponseDto response = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDto> assignShipment(
            @PathVariable Integer id,
            @RequestParam Integer storerId) {
        ShipmentResponseDto response = shipmentService.assignShipment(Long.valueOf(id), Long.valueOf(storerId));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDto> updateShipment(@PathVariable Integer id, @Valid @RequestBody UpdateShipmentDto updateDto) {
        ShipmentResponseDto response = shipmentService.updateShipment(Long.valueOf(id), updateDto);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/stored")
    @PreAuthorize("hasRole('STORER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDto> markAsStored(@PathVariable Integer id) {
        ShipmentResponseDto response = shipmentService.markAsStored(Long.valueOf(id));
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer id) {
        shipmentService.deleteShipment(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}