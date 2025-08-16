package com.example.wmsnew.picking.entity;

import com.example.wmsnew.common.enums.TaskStatus;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.order.entity.OrderItems;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "picking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PickingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picking_detail_id")
    private Long pickingDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private PickingAssignments assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItems orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "quantity_to_pick", nullable = false)
    private Integer quantityToPick;

    @Column(name = "picked_at")
    private LocalDateTime pickedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;
}