package com.example.wmsnew.supplier.repository;

import com.example.wmsnew.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository
    extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

  Optional<Supplier> findByEmail(String email);

  List<Supplier> findByName(String name);

  List<Supplier> findByCity(String city);

  List<Supplier> findByState(String state);

  List<Supplier> findByNameContainingIgnoreCase(String name);

  Long countByIsActiveTrue();
}
