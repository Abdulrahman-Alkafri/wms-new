package com.example.wmsnew.order.dto;

import com.example.wmsnew.common.enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Integer id;
    private String orderNumber;
    private OrderStatus status;
    private Long customerId;
    private String customerName;
    private Long pickerId;
    private String pickerName;
    private LocalDate requiredDate;
    private Integer totalItems;
    private Integer totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDto> items;
}