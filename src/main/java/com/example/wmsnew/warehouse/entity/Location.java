package com.example.wmsnew.warehouse.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.inventory.entity.Inventory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "location_code")
  private String locationCode;

  @ManyToOne
  @JoinColumn(name = "warehouse_id")
  private Warehouse warehouse;

  private String asile;
  private String rack;
  private String shelf;
  private String bin;

  @ManyToOne
  @JoinColumn(name = "standard_sizes_id")
  private StandardSizes standardSize;

  @Column(name = "current_load")
  private Double currentLoad = 0.0;

  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Inventory> inventories = new ArrayList<>();

  // Convenience
  public void addInventory(Inventory inv) {
    inventories.add(inv);
    inv.setLocation(this);
  }

  public void removeInventory(Inventory inv) {
    inventories.remove(inv);
    inv.setLocation(null);
  }
}
