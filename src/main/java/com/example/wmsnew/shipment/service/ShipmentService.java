package com.example.wmsnew.shipment.service;

import com.example.wmsnew.Exceptions.productExceptions.ProductNotFoundException;
import com.example.wmsnew.Exceptions.shipmentExceptions.ShipmentNotFoundException;
import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.inventory.InventoryService;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.product.repository.ProductRepository;
import com.example.wmsnew.shipment.dto.*;
import com.example.wmsnew.shipment.entity.Shipment;
import com.example.wmsnew.shipment.entity.ShipmentItems;
import com.example.wmsnew.shipment.entity.ShipmentPutaway;
import com.example.wmsnew.shipment.repository.ShipmentRepository;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.user.repository.UserRepository;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final LocationOptimizationService locationOptimizationService;
    private final BatchNumberService batchNumberService;
    private final ShipmentNumberGenerator shipmentNumberGenerator;
    private final LocationRepository locationRepository;
    
    @Transactional
    public ShipmentResponseDto createShipment(CreateShipmentDto createDto) {
        // Auto-generate shipment number if not provided
        String shipmentNumber = createDto.getShipmentNumber();
        if (shipmentNumber == null || shipmentNumber.trim().isEmpty()) {
            shipmentNumber = shipmentNumberGenerator.generateUniqueShipmentNumber();
            log.info("Auto-generated shipment number: {}", shipmentNumber);
        } else {
            log.info("Using provided shipment number: {}", shipmentNumber);
        }
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + createDto.getSupplierId()));
        
        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber(shipmentNumber);
        shipment.setSupplier(supplier);
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setTotalPrice(0);
        
        // First save to get the ID assigned
        shipment = shipmentRepository.save(shipment);
        
        // Process items AFTER shipment is saved and has an ID
        int totalPrice = 0;
        
        for (CreateShipmentItemDto itemDto : createDto.getItems()) {
            ShipmentItems item = processShipmentItem(shipment, itemDto);
            shipment.addItem(item);
            totalPrice += item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())).intValue();
        }
        
        // Update total price
        shipment.setTotalPrice(totalPrice);
        shipment = shipmentRepository.save(shipment);
        
        log.info("Shipment created successfully with id: {}", shipment.getId());
        return mapToResponseDto(shipment);
    }
    
    @Transactional
    private ShipmentItems processShipmentItem(Shipment shipment, CreateShipmentItemDto itemDto) {
        // Validate product exists
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemDto.getProductId()));
        
        // Create shipment item with automatic date generation
        LocalDate currentDate = LocalDate.now();
        LocalDate defaultExpiryDate = currentDate.plusYears(2); // Default 2 years shelf life
        
        ShipmentItems item = ShipmentItems.builder()
                .shipment(shipment)
                .product(product)
                .quantity(itemDto.getQuantity())
                .manufacturingDate(currentDate)  // Auto-set to current date
                .expiryDate(defaultExpiryDate)   // Auto-set to 2 years from now
                .unitCost(itemDto.getUnitCost())
                .build();
        
        // Generate batch number using timestamp and product info (shipment ID might be null at this point)
        String tempBatchNumber = batchNumberService.generateUniqueBatchNumber(
                shipment.getId() != null ? shipment.getId().intValue() : 0, 
                product.getId().intValue());
        item.setBatchNumber(tempBatchNumber);
        
        // Initialize putaway records list if null
        if (item.getPutawayRecords() == null) {
            item.setPutawayRecords(new ArrayList<>());
        }
        
        try {
            // Find optimal location
            Location optimalLocation = locationOptimizationService.findOptimalLocation(product, itemDto.getQuantity());
            
            // Create putaway entry
            ShipmentPutaway putaway = ShipmentPutaway.builder()
                    .shipmentItem(item)
                    .location(optimalLocation)
                    .quantity(itemDto.getQuantity())
                    .build();
            
            // Add putaway record to the shipment item
            item.getPutawayRecords().add(putaway);
            
            log.info("Item processed successfully: Product {}, Quantity {}, Location {}", 
                    product.getProductName(), itemDto.getQuantity(), optimalLocation.getLocationCode());
            
        } catch (Exception e) {
            log.warn("Could not find optimal location for product {}: {}. Creating default putaway record.", product.getProductName(), e.getMessage());
            
            // Create a default/temporary putaway record even if location optimization fails
            // This ensures that markAsStored doesn't fail later
            Location defaultLocation = getDefaultLocation();
            if (defaultLocation != null) {
                ShipmentPutaway putaway = ShipmentPutaway.builder()
                        .shipmentItem(item)
                        .location(defaultLocation)
                        .quantity(itemDto.getQuantity())
                        .build();
                
                item.getPutawayRecords().add(putaway);
                log.info("Created default putaway record for product {} at default location {}", 
                        product.getProductName(), defaultLocation.getLocationCode());
            } else {
                log.error("No default location available for product {}. This will cause issues during storage.", product.getProductName());
                throw new RuntimeException("Cannot create putaway record - no suitable location found for product: " + product.getProductName());
            }
        }
        
        return item;
    }
    
    public Page<ShipmentResponseDto> getShipments(ShipmentSearchCriteria criteria, Pageable pageable) {
        Specification<Shipment> spec = buildSpecification(criteria);
        Page<Shipment> shipments = shipmentRepository.findAll(spec, pageable);
        return shipments.map(this::mapToResponseDto);
    }
    
    public ShipmentResponseDto getShipmentById(Integer id) {
        Shipment shipment = shipmentRepository.findById(id.longValue())
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));
        return mapToResponseDto(shipment);
    }
    
    /**
     * Get putaway instructions for an assigned shipment.
     * This provides the storer with detailed information about where to store each item.
     */
    public List<ShipmentPutawayDto> getPutawayInstructions(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        // Only ASSIGNED or STORED shipments should have putaway instructions
        if (shipment.getStatus() == ShipmentStatus.PENDING) {
            throw new IllegalStateException("Putaway instructions are not available for PENDING shipments. Shipment must be assigned first.");
        }
        
        log.info("Retrieving putaway instructions for shipment {} ({})", shipmentId, shipment.getShipmentNumber());
        
        List<ShipmentPutawayDto> putawayInstructions = new ArrayList<>();
        
        for (ShipmentItems item : shipment.getItems()) {
            if (item.getPutawayRecords() != null && !item.getPutawayRecords().isEmpty()) {
                for (ShipmentPutaway putaway : item.getPutawayRecords()) {
                    ShipmentPutawayDto putawayDto = ShipmentPutawayDto.builder()
                            .id(putaway.getId())
                            .shipmentItemId(item.getId())
                            .productName(item.getProduct().getProductName())
                            .productSku("SKU-" + item.getProduct().getId())
                            .quantity(item.getQuantity())
                            .batchNumber(item.getBatchNumber())
                            .locationId(putaway.getLocation().getId())
                            .locationCode(putaway.getLocation().getLocationCode())
                            .locationDetails(formatLocationDetails(putaway.getLocation()))
                            .putawayQuantity(putaway.getQuantity())
                            .build();
                    
                    putawayInstructions.add(putawayDto);
                }
            } else {
                log.warn("No putaway records found for item {} in shipment {}. This should not happen for ASSIGNED/STORED shipments.", 
                        item.getId(), shipmentId);
            }
        }
        
        log.info("Retrieved {} putaway instructions for shipment {}", putawayInstructions.size(), shipmentId);
        return putawayInstructions;
    }
    
    @Transactional
    public ShipmentResponseDto assignShipment(Long shipmentId, Long storerId) {
        Shipment shipment = shipmentRepository.findById(shipmentId.longValue())
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        // Only PENDING shipments can be assigned
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("Only pending shipments can be assigned. Current status: " + shipment.getStatus());
        }
        
        User storer = userRepository.findById(storerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + storerId));
        
        log.info("Assigning shipment {} to storer {} and creating putaway records", shipmentId, storer.getName());
        
        // Ensure all shipment items have proper putaway records
        for (ShipmentItems item : shipment.getItems()) {
            ensurePutawayRecordsForItem(item);
        }
        
        shipment.setStorer(storer);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        
        shipment = shipmentRepository.save(shipment);
        log.info("Shipment {} successfully assigned to storer {} with all putaway records created", 
                shipmentId, storer.getName());
        
        return mapToResponseDto(shipment);
    }
    
    @Transactional
    public ShipmentResponseDto markAsStored(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        // Only ASSIGNED shipments can be marked as stored
        if (shipment.getStatus() != ShipmentStatus.ASSIGNED) {
            throw new IllegalStateException("Only assigned shipments can be marked as stored. Current status: " + shipment.getStatus());
        }
        
        log.info("Processing shipment storage for shipment: {}", shipmentId);
        
        // Create inventory entries for all shipment items
        for (ShipmentItems item : shipment.getItems()) {
            // Set received quantity to expected quantity (assuming full receipt)
            item.setReceivedQuantity(item.getQuantity());
            createInventoryFromShipmentItem(shipment, item);
        }
        
        shipment.setStatus(ShipmentStatus.STORED);
        shipment = shipmentRepository.save(shipment);
        
        log.info("Shipment {} marked as stored with inventory created", shipmentId);
        return mapToResponseDto(shipment);
    }
    
    private void createInventoryFromShipmentItem(Shipment shipment, ShipmentItems item) {
        log.info("Creating inventory for product: {} from shipment: {}", 
                item.getProduct().getProductName(), shipment.getShipmentNumber());
        
        // Find the putaway record for this item to get the location
        ShipmentPutaway putaway = item.getPutawayRecords().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No putaway record found for item: " + 
                        item.getProduct().getProductName()));
        
        // Use the inventory service to add inventory
        inventoryService.addInventory(
                item.getProduct(),
                putaway.getLocation(),
                item.getReceivedQuantity(),
                item.getBatchNumber(),
                item.getManufacturingDate(),
                item.getExpiryDate()
        );
    }
    
    @Transactional
    public ShipmentResponseDto updateShipment(Long shipmentId, UpdateShipmentDto updateDto) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        // Only PENDING shipments can be edited
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("Only pending shipments can be edited. Current status: " + shipment.getStatus());
        }
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(updateDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + updateDto.getSupplierId()));
        
        // Update shipment basic info
        shipment.setShipmentNumber(updateDto.getShipmentNumber());
        shipment.setSupplier(supplier);
        
        // Clear existing items and add new ones
        shipment.getItems().clear();
        
        int totalPrice = 0;
        for (CreateShipmentItemDto itemDto : updateDto.getItems()) {
            ShipmentItems item = processShipmentItem(shipment, itemDto);
            shipment.addItem(item);
            totalPrice += item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())).intValue();
        }
        
        shipment.setTotalPrice(totalPrice);
        shipment = shipmentRepository.save(shipment);
        
        log.info("Shipment {} updated successfully", shipmentId);
        return mapToResponseDto(shipment);
    }
    
    @Transactional
    public void deleteShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        log.info("Deleting shipment {} with status {}", shipmentId, shipment.getStatus());
        
        // Handle different shipment statuses appropriately
        switch (shipment.getStatus()) {
            case PENDING:
                log.info("Deleting PENDING shipment {} - no inventory to clean up", shipmentId);
                break;
                
            case ASSIGNED:
                log.warn("Deleting ASSIGNED shipment {} - storer assignment will be lost", shipmentId);
                break;
                
            case STORED:
                log.info("Deleting STORED shipment {} - removing associated inventory", shipmentId);
                removeAllInventoryForShipment(shipment);
                break;
        }
        
        // Safety check: Remove any inventory that might exist for this shipment
        for (ShipmentItems item : shipment.getItems()) {
            if (item.getReceivedQuantity() > 0) {
                log.info("Removing inventory for shipment item: {} from shipment {}", 
                        item.getProduct().getProductName(), shipmentId);
                removeInventoryForShipmentItem(shipment, item);
            }
        }
        
        // Delete the shipment (cascade will delete shipment items and putaway records)
        shipmentRepository.delete(shipment);
        log.info("Shipment {} deleted successfully", shipmentId);
    }
    
    private void removeAllInventoryForShipment(Shipment shipment) {
        log.info("Removing all inventory for STORED shipment: {} ({})", shipment.getId(), shipment.getShipmentNumber());
        
        for (ShipmentItems item : shipment.getItems()) {
            if (item.getPutawayRecords() != null && !item.getPutawayRecords().isEmpty()) {
                for (ShipmentPutaway putaway : item.getPutawayRecords()) {
                    // Find and remove the inventory based on batch number and location
                    List<Inventory> inventoryToRemove = inventoryRepository.findByProductAndLocationAndBatchNumber(
                            item.getProduct(), putaway.getLocation(), item.getBatchNumber());
                    
                    for (Inventory inventory : inventoryToRemove) {
                        log.info("Removing inventory: {} units of {} from location {} (batch: {})",
                                inventory.getQuantity(), item.getProduct().getProductName(),
                                putaway.getLocation().getLocationCode(), item.getBatchNumber());
                        inventoryRepository.delete(inventory);
                    }
                }
            }
        }
    }
    
    private void removeInventoryForShipmentItem(Shipment shipment, ShipmentItems item) {
        // This method is for safety - shouldn't be called for PENDING shipments
        // but provides protection if somehow inventory was created
        log.info("Checking for inventory to remove for product: {} from shipment: {}", 
                item.getProduct().getProductName(), shipment.getShipmentNumber());
        
        // For PENDING shipments, there should be no putaway records or inventory
        // This is just defensive programming
        if (item.getPutawayRecords() != null && !item.getPutawayRecords().isEmpty()) {
            log.warn("PENDING shipment {} has putaway records - this should not happen", shipment.getShipmentNumber());
            
            for (ShipmentPutaway putaway : item.getPutawayRecords()) {
                // Find and remove the inventory
                List<Inventory> inventoryToRemove = inventoryRepository.findByProductAndLocationAndBatchNumber(
                        item.getProduct(), putaway.getLocation(), item.getBatchNumber());
                
                for (Inventory inventory : inventoryToRemove) {
                    log.info("Removing inventory: {} units of {} from location {} (batch: {})",
                            inventory.getQuantity(), item.getProduct().getProductName(),
                            putaway.getLocation().getLocationCode(), item.getBatchNumber());
                    inventoryRepository.delete(inventory);
                }
            }
        }
    }
    
    private Specification<Shipment> buildSpecification(ShipmentSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            // Handle null criteria
            if (criteria != null) {
                if (criteria.getShipmentNumber() != null && !criteria.getShipmentNumber().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("shipmentNumber")), 
                            "%" + criteria.getShipmentNumber().toLowerCase() + "%"
                    ));
                }
                
                if (criteria.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
                }
                
                if (criteria.getSupplierId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("supplier").get("id"), criteria.getSupplierId()));
                }
                
                if (criteria.getStorerId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("storer").get("id"), criteria.getStorerId()));
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
    
    private ShipmentResponseDto mapToResponseDto(Shipment shipment) {
        return ShipmentResponseDto.builder()
                .id(shipment.getId())
                .shipmentNumber(shipment.getShipmentNumber())
                .status(shipment.getStatus())
                .supplierId(shipment.getSupplier().getId())
                .supplierName(shipment.getSupplier().getName())
                .storerId(shipment.getStorer() != null ? shipment.getStorer().getId() : null)
                .storerName(shipment.getStorer() != null ? shipment.getStorer().getName() : null)
                .totalPrice(shipment.getTotalPrice())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .items(shipment.getItems() != null ? 
                        shipment.getItems().stream()
                                .map(this::mapToItemResponseDto)
                                .toList() : List.of())
                .build();
    }
    
    private ShipmentItemResponseDto mapToItemResponseDto(ShipmentItems item) {
        return ShipmentItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .productSku("SKU-" + item.getProduct().getId())
                .quantity(item.getQuantity())
                .batchNumber(item.getBatchNumber())
                .manufacturingDate(item.getManufacturingDate())
                .expiryDate(item.getExpiryDate())
                .unitCost(item.getUnitCost())
                .build();
    }
    
    /**
     * Ensures that a shipment item has proper putaway records.
     * This method is called during shipment assignment to guarantee
     * that the storer knows exactly where to store each item.
     */
    private void ensurePutawayRecordsForItem(ShipmentItems item) {
        log.info("Ensuring putaway records for item: {} (Product: {})", 
                item.getId(), item.getProduct().getProductName());
        
        // Initialize putaway records list if null
        if (item.getPutawayRecords() == null) {
            item.setPutawayRecords(new ArrayList<>());
        }
        
        // If putaway records already exist, validate and potentially re-optimize
        if (!item.getPutawayRecords().isEmpty()) {
            log.info("Putaway records already exist for item {}. Validating locations.", item.getId());
            
            // Validate existing putaway records
            for (ShipmentPutaway putaway : item.getPutawayRecords()) {
                if (putaway.getLocation() == null) {
                    log.warn("Found putaway record with null location for item {}. Will recreate.", item.getId());
                    item.getPutawayRecords().clear();
                    break;
                }
            }
        }
        
        // Create putaway records if none exist or if they were cleared due to validation issues
        if (item.getPutawayRecords().isEmpty()) {
            log.info("Creating new putaway records for item {} (Product: {})", 
                    item.getId(), item.getProduct().getProductName());
            
            try {
                // Find optimal location for this item
                Location optimalLocation = locationOptimizationService.findOptimalLocation(
                        item.getProduct(), item.getQuantity());
                
                // Create putaway record
                ShipmentPutaway putaway = ShipmentPutaway.builder()
                        .shipmentItem(item)
                        .location(optimalLocation)
                        .quantity(item.getQuantity())
                        .build();
                
                item.getPutawayRecords().add(putaway);
                
                log.info("Created putaway record: Product {} -> Location {} (Quantity: {})", 
                        item.getProduct().getProductName(), 
                        optimalLocation.getLocationCode(), 
                        item.getQuantity());
                
            } catch (Exception e) {
                log.warn("Could not find optimal location for product {}: {}. Using default location.", 
                        item.getProduct().getProductName(), e.getMessage());
                
                // Fallback to default location
                Location defaultLocation = getDefaultLocation();
                if (defaultLocation != null) {
                    ShipmentPutaway putaway = ShipmentPutaway.builder()
                            .shipmentItem(item)
                            .location(defaultLocation)
                            .quantity(item.getQuantity())
                            .build();
                    
                    item.getPutawayRecords().add(putaway);
                    
                    log.info("Created default putaway record: Product {} -> Default Location {} (Quantity: {})", 
                            item.getProduct().getProductName(), 
                            defaultLocation.getLocationCode(), 
                            item.getQuantity());
                } else {
                    log.error("No locations available for putaway record creation for item {}", item.getId());
                    throw new RuntimeException("Cannot create putaway record - no locations available for product: " 
                            + item.getProduct().getProductName());
                }
            }
        } else {
            log.info("Putaway records validated for item {} - {} record(s) exist", 
                    item.getId(), item.getPutawayRecords().size());
        }
    }
    
    /**
     * Formats location details into a human-readable string for putaway instructions
     */
    private String formatLocationDetails(Location location) {
        StringBuilder details = new StringBuilder();
        
        if (location.getAsile() != null && !location.getAsile().trim().isEmpty()) {
            details.append("Aisle: ").append(location.getAsile());
        }
        
        if (location.getRack() != null && !location.getRack().trim().isEmpty()) {
            if (details.length() > 0) details.append(", ");
            details.append("Rack: ").append(location.getRack());
        }
        
        if (location.getShelf() != null && !location.getShelf().trim().isEmpty()) {
            if (details.length() > 0) details.append(", ");
            details.append("Shelf: ").append(location.getShelf());
        }
        
        if (location.getBin() != null && !location.getBin().trim().isEmpty()) {
            if (details.length() > 0) details.append(", ");
            details.append("Bin: ").append(location.getBin());
        }
        
        return details.toString();
    }
    
    /**
     * Get a default location for fallback when optimal location cannot be found
     * @return first available location or null if none exist
     */
    private Location getDefaultLocation() {
        try {
            List<Location> locations = locationRepository.findAll();
            if (!locations.isEmpty()) {
                Location defaultLocation = locations.get(0); // Use first available location as default
                log.info("Using default location: {} for fallback putaway", defaultLocation.getLocationCode());
                return defaultLocation;
            }
            log.warn("No locations found in the system for default fallback");
            return null;
        } catch (Exception e) {
            log.error("Error finding default location: {}", e.getMessage());
            return null;
        }
    }
}
