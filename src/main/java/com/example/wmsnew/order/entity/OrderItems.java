package com.example.wmsnew.order.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItems extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "inventory_id")
  private Inventory inventory;

  @Column(name = "requested_quantity")
  private Integer requestedQuantity;

  @Column(name = "picked_quantity")
  private Integer pickedQuantity = 0;

  @Column(name = "batch_number")
  private String batchNumber;

  @Column(name = "manufacturing_date")
  private LocalDate manufacturingDate;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  @Column(name = "unit_price")
  private BigDecimal unitPrice;

  @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItemAllocation> allocations;
}
