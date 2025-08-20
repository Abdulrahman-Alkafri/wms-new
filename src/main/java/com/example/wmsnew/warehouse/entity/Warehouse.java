package com.example.wmsnew.warehouse.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.order.entity.Order;
import com.example.wmsnew.shipment.entity.Shipment;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "warehouse_name")
  private String warehouseName;

  // Warehouse → Locations
  @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Location> locations = new ArrayList<>();

  // Convenience methods
  public void addLocation(Location location) {
    locations.add(location);
    location.setWarehouse(this);
  }

  public void removeLocation(Location location) {
    locations.remove(location);
    location.setWarehouse(null);
  }
}
