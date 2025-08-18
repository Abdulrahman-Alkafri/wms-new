package com.example.wmsnew.order.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.common.enums.OrderStatus;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
