package com.example.wmsnew.shipment.service;

import com.example.wmsnew.Exceptions.productExceptions.ProductNotFoundException;
import com.example.wmsnew.Exceptions.shipmentExceptions.ShipmentNotFoundException;
import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final LocationOptimizationService locationOptimizationService;
    private final BatchNumberService batchNumberService;
    
    @Transactional
    public ShipmentResponseDto createShipment(CreateShipmentDto createDto) {
        log.info("Creating shipment with number: {}", createDto.getShipmentNumber());
        
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + createDto.getSupplierId()));
        
        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber(createDto.getShipmentNumber());
        shipment.setSupplier(supplier);
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setTotalPrice(0);
        
        shipment = shipmentRepository.save(shipment);
        
        // Process items
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
        
        // Create shipment item
        ShipmentItems item = ShipmentItems.builder()
                .shipment(shipment)
                .product(product)
                .quantity(itemDto.getQuantity())
                .manufacturingDate(itemDto.getManufacturingDate())
                .expiryDate(itemDto.getExpiryDate())
                .unitCost(itemDto.getUnitCost())
                .build();
        
        // Generate batch number using timestamp and product info (will update with proper ID later if needed)
        String tempBatchNumber = batchNumberService.generateUniqueBatchNumber(shipment.getId().intValue(), product.getId().intValue());
        item.setBatchNumber(tempBatchNumber);
        
        try {
            // Find optimal location
            Location optimalLocation = locationOptimizationService.findOptimalLocation(product, itemDto.getQuantity());
            
            // Create putaway entry
            ShipmentPutaway putaway = ShipmentPutaway.builder()
                    .shipmentItem(item)
                    .location(optimalLocation)
                    .quantity(itemDto.getQuantity())
                    .build();
            
            // Create inventory entry in parallel
            Inventory inventory = Inventory.builder()
                    .product(product)
                    .location(optimalLocation)
                    .quantity(itemDto.getQuantity())
                    .batchNumber(tempBatchNumber)
                    .manufacturingDate(itemDto.getManufacturingDate())
                    .expiryDate(itemDto.getExpiryDate())
                    .build();
            
            inventoryRepository.save(inventory);
            
            // Update location current load
            optimalLocation.setCurrentLoad(optimalLocation.getCurrentLoad() + itemDto.getQuantity());
            
            log.info("Item processed successfully: Product {}, Quantity {}, Location {}", 
                    product.getProductName(), itemDto.getQuantity(), optimalLocation.getLocationCode());
            
        } catch (Exception e) {
            log.warn("Could not find optimal location for product {}: {}", product.getProductName(), e.getMessage());
            // Continue without location assignment for now
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
    
    @Transactional
    public ShipmentResponseDto assignShipment(Long shipmentId, Long storerId) {
        Shipment shipment = shipmentRepository.findById(shipmentId.longValue())
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + shipmentId));
        
        User storer = userRepository.findById(storerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + storerId));
        
        shipment.setStorer(storer);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        
        shipment = shipmentRepository.save(shipment);
        log.info("Shipment {} assigned to storer {}", shipmentId, storer.getName());
        
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
        
        shipment.setStatus(ShipmentStatus.STORED);
        shipment = shipmentRepository.save(shipment);
        
        log.info("Shipment {} marked as stored", shipmentId);
        return mapToResponseDto(shipment);
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
        
        // Only PENDING shipments can be deleted
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("Only pending shipments can be deleted. Current status: " + shipment.getStatus());
        }
        
        log.info("Deleting shipment {} with status {}", shipmentId, shipment.getStatus());
        shipmentRepository.delete(shipment);
        log.info("Shipment {} deleted successfully", shipmentId);
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
}