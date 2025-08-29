package com.example.wmsnew.order.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.warehouse.entity.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "order_picking")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPicking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "order_item_id")
  private OrderItems orderItem;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "picked_quantity")
  private Integer pickedQuantity;

  @Column(name = "batch_number")
  private String batchNumber;

  @Column(name = "manufacturing_date")
  private LocalDate manufacturingDate;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;
}
