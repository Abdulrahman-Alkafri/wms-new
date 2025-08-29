package com.example.wmsnew.inventory;

import com.example.wmsnew.Exceptions.inventoryException.InventoryNotFoundException;
import com.example.wmsnew.inventory.dto.InventoryResponseDto;
import com.example.wmsnew.inventory.dto.InventorySearchCriteria;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.inventory.repository.InventorySpecifications;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.warehouse.entity.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Page<InventoryResponseDto> getAllInventory(InventorySearchCriteria searchCriteria) {
        Sort sort = Sort.by(
            searchCriteria.getSortDir().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            searchCriteria.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(
            searchCriteria.getPage() - 1,
            searchCriteria.getSize(),
            sort
        );

        Specification<Inventory> spec = InventorySpecifications.withCriteria(searchCriteria);
        
        return inventoryRepository.findAll(spec, pageable)
            .map(this::mapToResponseDto);
    }

    public InventoryResponseDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
            .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with id: " + id));
        return mapToResponseDto(inventory);
    }

    private InventoryResponseDto mapToResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
            .id(inventory.getId())
            .productId(inventory.getProduct() != null ? inventory.getProduct().getId().longValue() : null)
            .productName(inventory.getProduct() != null ? inventory.getProduct().getProductName() : null)
            .brandName(inventory.getProduct() != null ? inventory.getProduct().getBrandName() : null)
            .categoryName(inventory.getProduct() != null && inventory.getProduct().getCategory() != null 
                ? inventory.getProduct().getCategory().getName() : null)
            .locationId(inventory.getLocation() != null ? inventory.getLocation().getId() : null)
            .locationCode(inventory.getLocation() != null ? inventory.getLocation().getLocationCode() : null)
            .warehouseName(inventory.getLocation() != null && inventory.getLocation().getWarehouse() != null 
                ? inventory.getLocation().getWarehouse().getWarehouseName() : null)
            .quantity(inventory.getQuantity())
            .batchNumber(inventory.getBatchNumber())
            .manufacturingDate(inventory.getManufacturingDate())
            .expiryDate(inventory.getExpiryDate())
            .createdAt(inventory.getCreatedAt())
            .updatedAt(inventory.getUpdatedAt())
            .build();
    }

    @Transactional
    public void addInventory(Product product, Location location, Integer quantity, String batchNumber, 
                            LocalDate manufacturingDate, LocalDate expiryDate) {
        log.info("Adding inventory: {} units of {} to location {} (batch: {})", 
                quantity, product.getProductName(), location.getLocationCode(), batchNumber);

        // Check if inventory already exists for this product, location, and batch
        List<Inventory> existingInventory = inventoryRepository.findByProductAndLocationAndBatchNumber(
                product, location, batchNumber);

        if (existingInventory.isEmpty()) {
            // Create new inventory record
            Inventory newInventory = Inventory.builder()
                    .product(product)
                    .location(location)
                    .quantity(quantity)
                    .batchNumber(batchNumber)
                    .manufacturingDate(manufacturingDate)
                    .expiryDate(expiryDate)
                    .build();

            inventoryRepository.save(newInventory);
            log.info("Created new inventory record: {} units at location {}", quantity, location.getLocationCode());
        } else {
            // Update existing inventory
            Inventory existing = existingInventory.get(0);
            existing.setQuantity(existing.getQuantity() + quantity);
            inventoryRepository.save(existing);
            log.info("Updated existing inventory: added {} units at location {}", quantity, location.getLocationCode());
        }

        // Update location current load
        location.setCurrentLoad(location.getCurrentLoad() + quantity);
    }

    @Transactional
    public void reserveInventory(Product product, Integer quantity) {
        log.info("Reserving {} units of {} from inventory", quantity, product.getProductName());

        // Find available inventory sorted by expiry date (nearest first)
        List<Inventory> availableInventory = inventoryRepository.findByProductIdAndQuantityGreaterThan(
                product.getId().longValue(), 0);

        // Sort by expiry date (nearest expiry first) then by location code
        availableInventory.sort((inv1, inv2) -> {
            if (inv1.getExpiryDate() == null && inv2.getExpiryDate() == null) {
                return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
            }
            if (inv1.getExpiryDate() == null) return 1;
            if (inv2.getExpiryDate() == null) return -1;
            
            int dateComparison = inv1.getExpiryDate().compareTo(inv2.getExpiryDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
        });

        Integer remainingQuantity = quantity;
        for (Inventory inventory : availableInventory) {
            if (remainingQuantity <= 0) break;

            Integer availableQuantity = inventory.getQuantity();
            Integer quantityToReserve = Math.min(remainingQuantity, availableQuantity);

            if (quantityToReserve > 0) {
                // Reduce inventory quantity
                inventory.setQuantity(availableQuantity - quantityToReserve);
                inventoryRepository.save(inventory);

                // Update location current load
                inventory.getLocation().setCurrentLoad(
                        inventory.getLocation().getCurrentLoad() - quantityToReserve);

                remainingQuantity -= quantityToReserve;

                log.info("Reserved {} units from location {} (batch: {})", 
                        quantityToReserve, inventory.getLocation().getLocationCode(), inventory.getBatchNumber());

                // Remove empty inventory records
                if (inventory.getQuantity() == 0) {
                    inventoryRepository.delete(inventory);
                    log.info("Removed empty inventory record for batch: {}", inventory.getBatchNumber());
                }
            }
        }

        if (remainingQuantity > 0) {
            log.warn("Could not fully reserve product {}. Short by {} units", 
                    product.getProductName(), remainingQuantity);
            throw new RuntimeException("Insufficient inventory for product " + product.getProductName() + 
                                     ". Short by " + remainingQuantity + " units");
        }
    }

    public List<InventoryResponseDto> getAvailableInventoryByProduct(Integer productId) {
        log.info("Getting available inventory for product ID: {}", productId);
        
        List<Inventory> inventoryList = inventoryRepository.findByProductIdAndQuantityGreaterThan(
                productId.longValue(), 0);
        
        // Sort by expiry date (nearest first) then by location code
        inventoryList.sort((inv1, inv2) -> {
            if (inv1.getExpiryDate() == null && inv2.getExpiryDate() == null) {
                return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
            }
            if (inv1.getExpiryDate() == null) return 1;
            if (inv2.getExpiryDate() == null) return -1;
            
            int dateComparison = inv1.getExpiryDate().compareTo(inv2.getExpiryDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
        });
        
        return inventoryList.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public void reserveSpecificInventory(Long inventoryId, Integer quantity) {
        log.info("Reserving {} units from specific inventory ID: {}", quantity, inventoryId);
        
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with id: " + inventoryId));
        
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory at location " + 
                    inventory.getLocation().getLocationCode() + ". Available: " + 
                    inventory.getQuantity() + ", Requested: " + quantity);
        }
        
        // Reduce inventory quantity
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
        
        // Update location current load
        inventory.getLocation().setCurrentLoad(
                inventory.getLocation().getCurrentLoad() - quantity);
        
        log.info("Reserved {} units from location {} (batch: {})", 
                quantity, inventory.getLocation().getLocationCode(), inventory.getBatchNumber());
        
        // Remove empty inventory records
        if (inventory.getQuantity() == 0) {
            inventoryRepository.delete(inventory);
            log.info("Removed empty inventory record for batch: {}", inventory.getBatchNumber());
        }
    }

    @Transactional
    public InventoryResponseDto updateInventoryQuantity(Long inventoryId, Integer newQuantity) {
        log.info("Updating inventory ID {} to new quantity: {}", inventoryId, newQuantity);
        
        try {
            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with ID: " + inventoryId));
            
            int oldQuantity = inventory.getQuantity();
            int quantityDifference = newQuantity - oldQuantity;
            
            log.info("Found inventory: ID={}, current quantity={}, new quantity={}, difference={}", 
                    inventoryId, oldQuantity, newQuantity, quantityDifference);
            
            // Validate new quantity
            if (newQuantity < 0) {
                throw new IllegalArgumentException("New quantity cannot be negative: " + newQuantity);
            }
            
            // Update inventory quantity
            inventory.setQuantity(newQuantity);
            
            // Update location current load safely
            if (inventory.getLocation() != null) {
                Location location = inventory.getLocation();
                double newCurrentLoad = location.getCurrentLoad() + quantityDifference;
                location.setCurrentLoad(Math.max(0.0, newCurrentLoad)); // Ensure non-negative
                log.info("Updated location {} current load by {}", location.getLocationCode(), quantityDifference);
            }
            
            // Save the updated inventory
            Inventory savedInventory = inventoryRepository.save(inventory);
            
            log.info("Successfully updated inventory ID {} from {} to {} units", 
                    inventoryId, oldQuantity, newQuantity);
            
            // Remove inventory record if quantity becomes 0
            if (newQuantity == 0) {
                InventoryResponseDto response = mapToResponseDto(savedInventory);
                inventoryRepository.delete(savedInventory);
                log.info("Removed empty inventory record ID: {}", inventoryId);
                return response;
            }
            
            return mapToResponseDto(savedInventory);
            
        } catch (Exception e) {
            log.error("Error updating inventory ID {} to quantity {}: {}", inventoryId, newQuantity, e.getMessage(), e);
            throw e;
        }
    }
}