package com.example.wmsnew.warehouse.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    private String address;

    private String city;

    private String state;

    @Column(name = "area_size")
    private BigDecimal areaSize;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}