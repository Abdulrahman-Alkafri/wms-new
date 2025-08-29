package com.example.wmsnew.order.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    private Long customerId;
    private String orderNumber;
    private LocalDate requiredDate;
    private List<CreateOrderItemDto> items;
}