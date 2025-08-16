package com.example.wmsnew.customer.repository;

import com.example.wmsnew.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByName(String name);
    
    List<Customer> findByCity(String city);
    
    List<Customer> findByState(String state);
    
    List<Customer> findByNameContainingIgnoreCase(String name);
}