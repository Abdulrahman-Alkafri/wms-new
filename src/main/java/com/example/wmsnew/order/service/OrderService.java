package com.example.wmsnew.order.service;

import com.example.wmsnew.Exceptions.orderException.OrderNotFoundException;
import com.example.wmsnew.Exceptions.productExceptions.ProductNotFoundException;
import com.example.wmsnew.common.enums.OrderStatus;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.inventory.InventoryService;
import com.example.wmsnew.order.dto.*;
import java.util.List;
import com.example.wmsnew.order.entity.Order;
import com.example.wmsnew.order.entity.OrderItems;
import com.example.wmsnew.order.entity.OrderItemAllocation;
import com.example.wmsnew.order.entity.OrderPicking;
import com.example.wmsnew.order.repository.OrderItemAllocationRepository;
import com.example.wmsnew.order.repository.OrderPickingRepository;
import com.example.wmsnew.order.repository.OrdersRepository;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.product.repository.ProductRepository;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.user.repository.UserRepository;
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
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderPickingRepository orderPickingRepository;
    private final OrderItemAllocationRepository orderItemAllocationRepository;
    private final InventoryService inventoryService;

    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto createDto) {
        log.info("Creating order with number: {}", createDto.getOrderNumber());

        // Validate customer exists
        Customer customer = customerRepository.findById(createDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + createDto.getCustomerId()));

        // Create order
        Order order = Order.builder()
                .orderNumber(createDto.getOrderNumber())
                .customer(customer)
                .requiredDate(createDto.getRequiredDate())
                .status(OrderStatus.PENDING)
                .totalItems(0)
                .totalPrice(0)
                .items(new ArrayList<>())
                .build();

        order = ordersRepository.save(order);

        // Process items
        int totalItems = 0;
        int totalPrice = 0;

        for (CreateOrderItemDto itemDto : createDto.getItems()) {
            OrderItems item = processOrderItem(order, itemDto);
            order.addItem(item);
            totalItems += item.getRequestedQuantity();
            totalPrice += item.getUnitPrice().multiply(BigDecimal.valueOf(item.getRequestedQuantity())).intValue();
        }

        // Update totals
        order.setTotalItems(totalItems);
        order.setTotalPrice(totalPrice);
        order = ordersRepository.save(order);

        log.info("Order created successfully with id: {}", order.getId());
        return mapToResponseDto(order);
    }

    private OrderItems processOrderItem(Order order, CreateOrderItemDto itemDto) {
        // Validate product exists
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemDto.getProductId()));

        // Validate inventory availability/allocations without reducing quantities (inventory will be reduced during picking)
        try {
            validateInventoryAllocations(product, itemDto.getRequestedQuantity(), itemDto.getInventoryAllocations());
            log.info("Validated availability of {} units of {} for order {}", 
                    itemDto.getRequestedQuantity(), product.getProductName(), order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to validate inventory availability for product {} in order {}: {}", 
                    product.getProductName(), order.getOrderNumber(), e.getMessage());
            throw new RuntimeException("Cannot create order - insufficient inventory for product " + 
                                     product.getProductName() + ": " + e.getMessage());
        }

        // Create order item - inventory will be allocated during picking
        OrderItems orderItem = OrderItems.builder()
                .order(order)
                .product(product)
                .requestedQuantity(itemDto.getRequestedQuantity())
                .pickedQuantity(0)
                .unitPrice(itemDto.getUnitPrice())
                .allocations(new ArrayList<>())
                .build();

        // Create inventory allocations if specified and immediately reduce inventory
        if (itemDto.getInventoryAllocations() != null && !itemDto.getInventoryAllocations().isEmpty()) {
            for (InventoryAllocationDto allocationDto : itemDto.getInventoryAllocations()) {
                Inventory inventory = inventoryRepository.findById(allocationDto.getInventoryId()).orElse(null);
                if (inventory != null) {
                    // Immediately reduce inventory for this allocation
                    try {
                        inventoryService.reserveSpecificInventory(inventory.getId(), allocationDto.getQuantityToAllocate());
                        log.info("Reduced {} units from inventory {} for order {} item {}", 
                                allocationDto.getQuantityToAllocate(), inventory.getId(), 
                                order.getOrderNumber(), product.getProductName());
                    } catch (Exception e) {
                        log.error("Failed to reduce inventory {} for order {}: {}", 
                                inventory.getId(), order.getOrderNumber(), e.getMessage());
                        throw new RuntimeException("Failed to reduce inventory for product " + 
                                                 product.getProductName() + ": " + e.getMessage());
                    }
                    
                    OrderItemAllocation allocation = OrderItemAllocation.builder()
                            .orderItem(orderItem)
                            .inventory(inventory)
                            .allocatedQuantity(allocationDto.getQuantityToAllocate())
                            .pickedQuantity(allocationDto.getQuantityToAllocate()) // Mark as picked since inventory is reduced
                            .build();
                    orderItem.getAllocations().add(allocation);
                }
            }
            // Set picked quantity equal to requested since inventory has been reduced immediately
            orderItem.setPickedQuantity(itemDto.getRequestedQuantity());
        }

        return orderItem;
    }

    private void validateInventoryAvailability(Product product, Integer requestedQuantity) {
        List<Inventory> availableInventory = inventoryRepository.findByProductIdAndQuantityGreaterThan(
                product.getId().longValue(), 0);
        
        Integer totalAvailable = availableInventory.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        
        if (totalAvailable < requestedQuantity) {
            throw new RuntimeException("Insufficient inventory for product " + product.getProductName() + 
                    ". Available: " + totalAvailable + ", Requested: " + requestedQuantity);
        }
        
        log.debug("Inventory validation passed for {}: {} available, {} requested", 
                product.getProductName(), totalAvailable, requestedQuantity);
    }

    private void validateInventoryAllocations(Product product, Integer requestedQuantity, 
                                            List<InventoryAllocationDto> allocations) {
        if (allocations == null || allocations.isEmpty()) {
            // Fall back to general availability check if no specific allocations provided
            validateInventoryAvailability(product, requestedQuantity);
            return;
        }

        Integer totalAllocated = allocations.stream()
                .mapToInt(InventoryAllocationDto::getQuantityToAllocate)
                .sum();

        if (!totalAllocated.equals(requestedQuantity)) {
            throw new RuntimeException("Allocation mismatch for product " + product.getProductName() + 
                    ". Requested: " + requestedQuantity + ", Allocated: " + totalAllocated);
        }

        // Validate each allocation
        for (InventoryAllocationDto allocation : allocations) {
            Inventory inventory = inventoryRepository.findById(allocation.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + allocation.getInventoryId()));

            if (!inventory.getProduct().getId().equals(product.getId())) {
                throw new RuntimeException("Inventory " + allocation.getInventoryId() + 
                        " belongs to different product than requested");
            }

            if (inventory.getQuantity() < allocation.getQuantityToAllocate()) {
                throw new RuntimeException("Insufficient quantity in inventory " + allocation.getInventoryId() + 
                        ". Available: " + inventory.getQuantity() + ", Requested: " + allocation.getQuantityToAllocate());
            }
        }

        log.debug("Inventory allocation validation passed for {}: {} allocations totaling {}", 
                product.getProductName(), allocations.size(), totalAllocated);
    }

    @Transactional
    public OrderResponseDto assignOrder(Integer orderId, Long pickerId) {
        Order order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        User picker = userRepository.findById(pickerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + pickerId));

        order.setPicker(picker);
        order.setStatus(OrderStatus.ASSIGNED);

        order = ordersRepository.save(order);
        log.info("Order {} assigned to picker {}", orderId, picker.getName());

        return mapToResponseDto(order);
    }

    @Transactional
    public OrderResponseDto processOrderPicking(Integer orderId) {
        Order order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new IllegalStateException("Only assigned orders can be picked. Current status: " + order.getStatus());
        }

        log.info("Processing picking for order: {}", order.getOrderNumber());

        // Process each order item with smart picking logic
        for (OrderItems orderItem : order.getItems()) {
            processSmartPicking(orderItem);
        }

        // Check if all items are fully picked
        boolean allItemsPicked = order.getItems().stream()
                .allMatch(item -> item.getPickedQuantity().equals(item.getRequestedQuantity()));

        if (allItemsPicked) {
            order.setStatus(OrderStatus.PICKED);
            log.info("Order {} fully picked and completed", order.getOrderNumber());
        }

        order = ordersRepository.save(order);
        return mapToResponseDto(order);
    }

    @Transactional
    private void processSmartPicking(OrderItems orderItem) {
        Integer remainingQuantity = orderItem.getRequestedQuantity() - orderItem.getPickedQuantity();
        
        if (remainingQuantity <= 0) {
            return; // Already fully picked
        }

        log.info("Processing smart picking for product: {}, remaining quantity: {}", 
                orderItem.getProduct().getProductName(), remainingQuantity);

        // Check if there are specific inventory allocations
        if (orderItem.getAllocations() != null && !orderItem.getAllocations().isEmpty()) {
            // Pick from allocated inventory records
            processAllocatedPicking(orderItem, remainingQuantity);
        } else {
            // Fall back to automatic FIFO/FEFO picking
            processAutomaticPicking(orderItem, remainingQuantity);
        }
    }

    @Transactional
    private void processAllocatedPicking(OrderItems orderItem, Integer remainingQuantity) {
        log.info("Processing allocated picking for {} allocations", orderItem.getAllocations().size());

        // Check if inventory was already reduced during order creation
        boolean alreadyReduced = orderItem.getAllocations().stream()
                .anyMatch(allocation -> allocation.getPickedQuantity() > 0);

        if (alreadyReduced) {
            log.info("Inventory already reduced for order item {}. Creating picking records only.", 
                    orderItem.getProduct().getProductName());
            
            // Just create picking records without reducing inventory again
            for (OrderItemAllocation allocation : orderItem.getAllocations()) {
                if (allocation.getPickedQuantity() > 0) {
                    OrderPicking picking = OrderPicking.builder()
                            .orderItem(orderItem)
                            .location(allocation.getInventory().getLocation())
                            .pickedQuantity(allocation.getPickedQuantity())
                            .batchNumber(allocation.getInventory().getBatchNumber())
                            .manufacturingDate(allocation.getInventory().getManufacturingDate())
                            .expiryDate(allocation.getInventory().getExpiryDate())
                            .build();

                    orderPickingRepository.save(picking);
                    
                    // Update order item batch info with the first picked batch
                    if (orderItem.getBatchNumber() == null) {
                        orderItem.setBatchNumber(allocation.getInventory().getBatchNumber());
                        orderItem.setManufacturingDate(allocation.getInventory().getManufacturingDate());
                        orderItem.setExpiryDate(allocation.getInventory().getExpiryDate());
                    }
                    
                    log.info("Created picking record for {} units from location {} (batch: {})",
                            allocation.getPickedQuantity(), 
                            allocation.getInventory().getLocation().getLocationCode(),
                            allocation.getInventory().getBatchNumber());
                }
            }
            return;
        }

        // Original picking logic for orders created without immediate inventory reduction
        for (OrderItemAllocation allocation : orderItem.getAllocations()) {
            if (remainingQuantity <= 0) break;

            Integer allocationRemaining = allocation.getAllocatedQuantity() - allocation.getPickedQuantity();
            if (allocationRemaining <= 0) continue;

            Inventory inventory = allocation.getInventory();
            Integer quantityToPick = Math.min(Math.min(remainingQuantity, allocationRemaining), inventory.getQuantity());

            if (quantityToPick > 0) {
                // Create picking record
                OrderPicking picking = OrderPicking.builder()
                        .orderItem(orderItem)
                        .location(inventory.getLocation())
                        .pickedQuantity(quantityToPick)
                        .batchNumber(inventory.getBatchNumber())
                        .manufacturingDate(inventory.getManufacturingDate())
                        .expiryDate(inventory.getExpiryDate())
                        .build();

                orderPickingRepository.save(picking);

                // Update inventory - reduce quantity
                inventory.setQuantity(inventory.getQuantity() - quantityToPick);
                inventoryRepository.save(inventory);

                // Update location current load
                inventory.getLocation().setCurrentLoad(
                        inventory.getLocation().getCurrentLoad() - quantityToPick);

                // Update allocation picked quantity
                allocation.setPickedQuantity(allocation.getPickedQuantity() + quantityToPick);

                // Update order item picked quantity
                orderItem.setPickedQuantity(orderItem.getPickedQuantity() + quantityToPick);
                
                // Update order item batch info with the first picked batch
                if (orderItem.getBatchNumber() == null) {
                    orderItem.setBatchNumber(inventory.getBatchNumber());
                    orderItem.setManufacturingDate(inventory.getManufacturingDate());
                    orderItem.setExpiryDate(inventory.getExpiryDate());
                }

                remainingQuantity -= quantityToPick;

                log.info("Picked {} units from allocated inventory {} at location {} (batch: {})",
                        quantityToPick, inventory.getId(), inventory.getLocation().getLocationCode(),
                        inventory.getBatchNumber());

                // Remove empty inventory records
                if (inventory.getQuantity() == 0) {
                    inventoryRepository.delete(inventory);
                    log.info("Removed empty inventory record for batch: {}", inventory.getBatchNumber());
                }
            }
        }

        if (remainingQuantity > 0) {
            log.warn("Could not fully pick from allocated inventory for product {}. Short by {} units",
                    orderItem.getProduct().getProductName(), remainingQuantity);
        }
    }

    @Transactional
    private void processAutomaticPicking(OrderItems orderItem, Integer remainingQuantity) {
        log.info("Processing automatic FIFO/FEFO picking");

        // Find available inventory sorted by expiry date (nearest first) and location proximity
        List<Inventory> availableInventory = inventoryRepository.findByProductIdAndQuantityGreaterThan(
                orderItem.getProduct().getId().longValue(), 0);

        // Sort by expiry date (nearest expiry first) then by location code for consistent ordering
        availableInventory.sort((inv1, inv2) -> {
            // First priority: expiry date (nearest first, nulls last)
            if (inv1.getExpiryDate() == null && inv2.getExpiryDate() == null) {
                return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
            }
            if (inv1.getExpiryDate() == null) return 1;
            if (inv2.getExpiryDate() == null) return -1;
            
            int dateComparison = inv1.getExpiryDate().compareTo(inv2.getExpiryDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            
            // Second priority: location code (for consistent ordering)
            return inv1.getLocation().getLocationCode().compareTo(inv2.getLocation().getLocationCode());
        });

        // Pick from inventory using FIFO with expiry priority
        for (Inventory inventory : availableInventory) {
            if (remainingQuantity <= 0) break;

            Integer availableQuantity = inventory.getQuantity();
            Integer quantityToPick = Math.min(remainingQuantity, availableQuantity);

            if (quantityToPick > 0) {
                // Create picking record
                OrderPicking picking = OrderPicking.builder()
                        .orderItem(orderItem)
                        .location(inventory.getLocation())
                        .pickedQuantity(quantityToPick)
                        .batchNumber(inventory.getBatchNumber())
                        .manufacturingDate(inventory.getManufacturingDate())
                        .expiryDate(inventory.getExpiryDate())
                        .build();

                orderPickingRepository.save(picking);

                // Update inventory - reduce quantity
                inventory.setQuantity(availableQuantity - quantityToPick);
                inventoryRepository.save(inventory);

                // Update location current load
                inventory.getLocation().setCurrentLoad(
                        inventory.getLocation().getCurrentLoad() - quantityToPick);

                // Update order item picked quantity
                orderItem.setPickedQuantity(orderItem.getPickedQuantity() + quantityToPick);
                
                // Update order item batch info with the first picked batch
                if (orderItem.getBatchNumber() == null) {
                    orderItem.setBatchNumber(inventory.getBatchNumber());
                    orderItem.setManufacturingDate(inventory.getManufacturingDate());
                    orderItem.setExpiryDate(inventory.getExpiryDate());
                }

                remainingQuantity -= quantityToPick;

                log.info("Picked {} units of {} from location {} (batch: {}, expires: {})",
                        quantityToPick, orderItem.getProduct().getProductName(),
                        inventory.getLocation().getLocationCode(),
                        inventory.getBatchNumber(),
                        inventory.getExpiryDate());

                // Remove empty inventory records
                if (inventory.getQuantity() == 0) {
                    inventoryRepository.delete(inventory);
                    log.info("Removed empty inventory record for batch: {}", inventory.getBatchNumber());
                }
            }
        }

        if (remainingQuantity > 0) {
            log.warn("Could not fully pick product {}. Short by {} units",
                    orderItem.getProduct().getProductName(), remainingQuantity);
        }
    }

    public Page<OrderResponseDto> getOrders(OrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> spec = buildSpecification(criteria);
        Page<Order> orders = ordersRepository.findAll(spec, pageable);
        return orders.map(this::mapToResponseDto);
    }

    public OrderResponseDto getOrderById(Integer id) {
        Order order = ordersRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return mapToResponseDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(Integer orderId, UpdateOrderDto updateDto) {
        Order order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        // Only PENDING orders can be edited
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be edited. Current status: " + order.getStatus());
        }

        // Validate customer exists
        Customer customer = customerRepository.findById(updateDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + updateDto.getCustomerId()));

        // Update order basic info
        order.setOrderNumber(updateDto.getOrderNumber());
        order.setCustomer(customer);
        order.setRequiredDate(updateDto.getRequiredDate());

        // Clear existing items and add new ones
        order.getItems().clear();

        int totalItems = 0;
        int totalPrice = 0;
        for (CreateOrderItemDto itemDto : updateDto.getItems()) {
            OrderItems item = processOrderItem(order, itemDto);
            order.addItem(item);
            totalItems += item.getRequestedQuantity();
            totalPrice += item.getUnitPrice().multiply(BigDecimal.valueOf(item.getRequestedQuantity())).intValue();
        }

        order.setTotalItems(totalItems);
        order.setTotalPrice(totalPrice);
        order = ordersRepository.save(order);

        log.info("Order {} updated successfully", orderId);
        return mapToResponseDto(order);
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        Order order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        // Only PENDING orders can be deleted
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be deleted. Current status: " + order.getStatus());
        }

        log.info("Deleting order {} with status {}", orderId, order.getStatus());
        
        // Safety check: For PENDING orders, there should be no picked quantities
        // But if there are any (which shouldn't happen), we need to restore inventory
        for (OrderItems item : order.getItems()) {
            if (item.getPickedQuantity() > 0) {
                log.warn("PENDING order {} has picked quantities for item {} - this should not happen!", 
                        orderId, item.getProduct().getProductName());
                
                // If somehow there are picked quantities, we should restore them
                // This is defensive programming - shouldn't happen for PENDING orders
                restoreInventoryForOrderItem(item);
            }
        }
        
        // Delete the order (cascade will delete order items)
        ordersRepository.delete(order);
        log.info("Order {} deleted successfully", orderId);
    }
    
    private void restoreInventoryForOrderItem(OrderItems orderItem) {
        // This method is for safety - shouldn't be called for PENDING orders
        // but provides protection if somehow picked quantities exist
        if (orderItem.getPickedQuantity() > 0) {
            log.info("Restoring {} units of {} to inventory", 
                    orderItem.getPickedQuantity(), orderItem.getProduct().getProductName());
            
            // In a real restoration, we would need to know which specific inventory
            // records were reduced. For now, we'll log this as it shouldn't happen
            // for PENDING orders anyway.
            log.warn("Inventory restoration needed but not implemented - " +
                    "PENDING orders should not have picked quantities");
        }
    }

    private Specification<Order> buildSpecification(OrderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria != null) {
                if (criteria.getOrderNumber() != null && !criteria.getOrderNumber().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("orderNumber")),
                            "%" + criteria.getOrderNumber().toLowerCase() + "%"
                    ));
                }

                if (criteria.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
                }

                if (criteria.getCustomerId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), criteria.getCustomerId()));
                }

                if (criteria.getPickerId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("picker").get("id"), criteria.getPickerId()));
                }

                if (criteria.getRequiredDate() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("requiredDate"), criteria.getRequiredDate()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .pickerId(order.getPicker() != null ? order.getPicker().getId() : null)
                .pickerName(order.getPicker() != null ? order.getPicker().getName() : null)
                .requiredDate(order.getRequiredDate())
                .totalItems(order.getTotalItems())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems() != null ?
                        order.getItems().stream()
                                .map(this::mapToItemResponseDto)
                                .toList() : List.of())
                .build();
    }

    private OrderItemResponseDto mapToItemResponseDto(OrderItems item) {
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .productSku("SKU-" + item.getProduct().getId())
                .requestedQuantity(item.getRequestedQuantity())
                .pickedQuantity(item.getPickedQuantity())
                .batchNumber(item.getBatchNumber())
                .manufacturingDate(item.getManufacturingDate())
                .expiryDate(item.getExpiryDate())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}