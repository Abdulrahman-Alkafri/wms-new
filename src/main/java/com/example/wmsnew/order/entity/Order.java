package com.example.wmsnew.order.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.common.enums.OrderStatus;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "order_number", unique = true, nullable = false)
  private String orderNumber;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "picker_id")
  private User picker;

  @Column(name = "required_date")
  private LocalDate requiredDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(name = "total_items")
  private Integer totalItems;

  @Column(name = "total_price")
  private Integer totalPrice;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItems> items = new ArrayList<>();

  // Convenience
  public void addItem(OrderItems item) {
    items.add(item);
    item.setOrder(this);
  }

  public void removeItem(OrderItems item) {
    items.remove(item);
    item.setOrder(null);
  }
}
