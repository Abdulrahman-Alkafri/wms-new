package com.example.wmsnew.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_standard_sizes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStandardSizes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_sizes_id", nullable = false)
    private StandardSizes standardSizes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "max_quantity")
    private Integer maxQuantity;
}