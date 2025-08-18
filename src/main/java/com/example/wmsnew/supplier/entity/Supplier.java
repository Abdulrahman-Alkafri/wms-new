package com.example.wmsnew.supplier.entity;

import com.example.wmsnew.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "contact_person")
  private String contactPerson;

  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  private String address;

  private String city;

  private String state;
}
