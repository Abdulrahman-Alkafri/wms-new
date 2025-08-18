package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shipment_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItems extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "shipment_id")
  private Shipment shipment;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private Integer quantity;

  @Column(name = "batch_number")
  private String batchNumber;

  @Column(name = "manufacturing_date")
  private LocalDate manufacturingDate;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  @Column(name = "unit_cost")
  private BigDecimal unitCost;
}
