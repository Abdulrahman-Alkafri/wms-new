package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.enums.QualityTestStatus;
import com.example.wmsnew.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quality_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QualityTests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tested_by_id", nullable = false)
    private User testedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_status", nullable = false)
    private QualityTestStatus overallStatus = QualityTestStatus.IN_PROGRESS;

    @Column(columnDefinition = "TEXT")
    private String notes;
}