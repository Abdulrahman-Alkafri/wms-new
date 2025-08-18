package com.example.wmsnew.product.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.order.entity.OrderItems;
import com.example.wmsnew.shipment.entity.ShipmentItems;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "brand_name")
  private String brandName;

  private String description;

  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  // ✅ Product ↔ ProductStandardSizes
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductStandardSizes> productStandardSizes = new ArrayList<>();

  public void addProductStandardSize(ProductStandardSizes pss) {
    productStandardSizes.add(pss);
    pss.setProduct(this);
  }

  public void removeProductStandardSize(ProductStandardSizes pss) {
    productStandardSizes.remove(pss);
    pss.setProduct(null);
  }

  // ✅ Product ↔ ShipmentItems
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ShipmentItems> shipmentItems = new ArrayList<>();

  public void addShipmentItem(ShipmentItems si) {
    shipmentItems.add(si);
    si.setProduct(this);
  }

  public void removeShipmentItem(ShipmentItems si) {
    shipmentItems.remove(si);
    si.setProduct(null);
  }

  // ✅ Product ↔ OrderItems
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItems> orderItems = new ArrayList<>();

  public void addOrderItem(OrderItems oi) {
    orderItems.add(oi);
    oi.setProduct(this);
  }

  public void removeOrderItem(OrderItems oi) {
    orderItems.remove(oi);
    oi.setProduct(null);
  }

  // ✅ Product ↔ Inventory
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Inventory> inventories = new ArrayList<>();

  public void addInventory(Inventory inv) {
    inventories.add(inv);
    inv.setProduct(this);
  }

  public void removeInventory(Inventory inv) {
    inventories.remove(inv);
    inv.setProduct(null);
  }
}
