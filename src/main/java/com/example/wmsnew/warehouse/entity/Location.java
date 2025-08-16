package com.example.wmsnew.warehouse.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.product.entity.StandardSizes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    private String rack;

    private String shelf;

    private String bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_sizes_id")
    private StandardSizes standardSizes;

    @Column(name = "current_load")
    private BigDecimal currentLoad;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;
}