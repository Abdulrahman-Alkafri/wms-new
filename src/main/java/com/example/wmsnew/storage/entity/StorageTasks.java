package com.example.wmsnew.storage.entity;

import com.example.wmsnew.common.enums.TaskStatus;
import com.example.wmsnew.shipment.entity.ShipmentItems;
import com.example.wmsnew.warehouse.entity.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "storage_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private StorageAssignments assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_item_id", nullable = false)
    private ShipmentItems shipmentItem;

    @Column(name = "quantity_to_store", nullable = false)
    private Integer quantityToStore;

    @Column(name = "quantity_stored")
    private Integer quantityStored;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_location_id", nullable = false)
    private Location targetLocation;

    @Column(name = "stored_at")
    private LocalDateTime storedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;
}