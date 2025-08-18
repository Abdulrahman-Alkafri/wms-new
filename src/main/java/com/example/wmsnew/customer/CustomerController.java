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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CreateCustomerDto dto) {
    CustomerResponseDto response = customerService.createCustomer(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<CustomerResponseDto>> findAllCustomers(
      @ModelAttribute CustomerBaseSearchCriteria criteria) {
    Page<CustomerResponseDto> customers = customerService.findAllCustomers(criteria);
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
    CustomerResponseDto customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(customer);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(
      @PathVariable Long id, @Valid @RequestBody UpdateCustomerDto dto) {
    Customer updated = customerService.updateCustomer(id, dto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }
}
