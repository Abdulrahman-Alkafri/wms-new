package com.example.wmsnew.customer;

import com.example.wmsnew.customer.dtos.CreateCustomerDto;
import com.example.wmsnew.customer.dtos.CustomerBaseSearchCriteria;
import com.example.wmsnew.customer.dtos.CustomerResponseDto;
import com.example.wmsnew.customer.dtos.UpdateCustomerDto;
import com.example.wmsnew.customer.entity.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CreateCustomerDto dto) {
    CustomerResponseDto response = customerService.createCustomer(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<Page<CustomerResponseDto>> findAllCustomers(
      @ModelAttribute CustomerBaseSearchCriteria criteria) {
    Page<CustomerResponseDto> customers = customerService.findAllCustomers(criteria);
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
  public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
    CustomerResponseDto customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(customer);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Customer> updateCustomer(
      @PathVariable Long id, @Valid @RequestBody UpdateCustomerDto dto) {
    Customer updated = customerService.updateCustomer(id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }
}
