package com.example.wmsnew.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "standard_sizes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardSizes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer width;

    private Integer height;

    private Integer depth;

    private Integer volume;

    @Column(name = "max_weight")
    private Integer maxWeight;
}