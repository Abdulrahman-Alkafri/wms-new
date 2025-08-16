package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "shipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "shipment_number", nullable = false)
    private String shipmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    private String carrier;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}