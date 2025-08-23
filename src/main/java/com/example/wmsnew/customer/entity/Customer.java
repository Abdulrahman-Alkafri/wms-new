package com.example.wmsnew.customer.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  private String address;

  private String city;

  private String state;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;
}
