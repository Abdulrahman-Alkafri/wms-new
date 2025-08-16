package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.enums.DamageType;
import com.example.wmsnew.common.enums.TestResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QualityTestResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_result_id")
    private Long testResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private QualityTests test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_item_id", nullable = false)
    private ShipmentItems shipmentItem;

    @Column(name = "tested_quantity", nullable = false)
    private Integer testedQuantity;

    @Column(name = "passed_quantity", nullable = false)
    private Integer passedQuantity;

    @Column(name = "failed_quantity", nullable = false)
    private Integer failedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_status", nullable = false)
    private TestResultStatus testStatus;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "damage_type")
    private DamageType damageType;

    @Column(name = "damage_description", columnDefinition = "TEXT")
    private String damageDescription;

    @Column(name = "tested_at", nullable = false)
    private LocalDateTime testedAt = LocalDateTime.now();
}