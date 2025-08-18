package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment_putaway")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentPutaway extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "shipment_item_id")
  private ShipmentItems shipmentItem;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  private Integer quantity;
}
