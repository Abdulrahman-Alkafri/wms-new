package com.example.wmsnew.shipment.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.common.enums.ShipmentStatus;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shipment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  @Column(name = "shipment_number")
  private String shipmentNumber;

  @Enumerated(EnumType.STRING)
  private ShipmentStatus status;

  @ManyToOne
  @JoinColumn(name = "storer_id")
  private User storer;

  @Column(name = "total_price")
  private Integer totalPrice;

  @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<ShipmentItems> items = new ArrayList<>();


  // Convenience
  public void addItem(ShipmentItems item) {
    items.add(item);
    item.setShipment(this);
  }

  public void removeItem(ShipmentItems item) {
    items.remove(item);
    item.setShipment(null);
  }
}
