package com.example.wmsnew.order;

import com.example.wmsnew.order.dto.*;
import com.example.wmsnew.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderDto createDto) {
        log.info("Creating new order: {}", createDto.getOrderNumber());
        OrderResponseDto order = orderService.createOrder(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER')")
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Long pickerId,
            @RequestParam(required = false) String pickerName,
            @RequestParam(required = false) String requiredDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        // Build search criteria
        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .orderNumber(orderNumber)
                .status(status != null ? com.example.wmsnew.common.enums.OrderStatus.valueOf(status.toUpperCase()) : null)
                .customerId(customerId)
                .customerName(customerName)
                .pickerId(pickerId)
                .pickerName(pickerName)
                .requiredDate(requiredDate != null ? java.time.LocalDate.parse(requiredDate) : null)
                .searchTerm(searchTerm)
                .build();

        // Build pageable
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderResponseDto> orders = orderService.getOrders(criteria, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER')")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Integer id) {
        log.info("Fetching order with id: {}", id);
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDto> assignOrder(
            @PathVariable Integer id,
            @RequestParam Long pickerId) {
        log.info("Assigning order {} to picker {}", id, pickerId);
        OrderResponseDto order = orderService.assignOrder(id, pickerId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/pick")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER')")
    public ResponseEntity<OrderResponseDto> processOrderPicking(@PathVariable Integer id) {
        log.info("Processing picking for order {}", id);
        OrderResponseDto order = orderService.processOrderPicking(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Integer id,
            @RequestBody UpdateOrderDto updateDto) {
        log.info("Updating order {}", id);
        OrderResponseDto order = orderService.updateOrder(id, updateDto);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        log.info("Deleting order {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}