package com.example.wmsnew.order.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.inventory.entity.Inventory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item_allocations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemAllocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItems orderItem;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Column(name = "allocated_quantity")
    private Integer allocatedQuantity;

    @Column(name = "picked_quantity")
    private Integer pickedQuantity = 0;
}