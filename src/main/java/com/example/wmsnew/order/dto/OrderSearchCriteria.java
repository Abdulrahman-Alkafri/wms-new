package com.example.wmsnew.order.dto;

import com.example.wmsnew.common.enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchCriteria {
    private String orderNumber;
    private OrderStatus status;
    private Long customerId;
    private String customerName;
    private Long pickerId;
    private String pickerName;
    private LocalDate requiredDate;
    private String searchTerm;
}