package com.example.wmsnew.customer;

import com.example.wmsnew.customer.dtos.CreateCustomerDto;
import com.example.wmsnew.customer.dtos.CustomerBaseSearchCriteria;
import com.example.wmsnew.customer.dtos.CustomerResponseDto;
import com.example.wmsnew.customer.dtos.UpdateCustomerDto;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.customer.repository.CustomerSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerResponseDto createCustomer(CreateCustomerDto dto) {
    Customer customer =
        Customer.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .phoneNumber(dto.getPhoneNumber())
            .address(dto.getAddress())
            .city(dto.getCity())
            .state(dto.getState())
            .build();

    Customer savedCustomer = customerRepository.save(customer);
    
    return CustomerResponseDto.builder()
        .id(savedCustomer.getId())
        .name(savedCustomer.getName())
        .email(savedCustomer.getEmail())
        .phoneNumber(savedCustomer.getPhoneNumber())
        .address(savedCustomer.getAddress())
        .city(savedCustomer.getCity())
        .state(savedCustomer.getState())
        .createdAt(savedCustomer.getCreatedAt().toString())
        .build();
  }

  public Page<CustomerResponseDto> findAllCustomers(CustomerBaseSearchCriteria cs) {
    Specification<Customer> customerSpecification;
    
    if (cs.getGlobalSearch() != null && !cs.getGlobalSearch().isEmpty()) {
      customerSpecification = CustomerSpecifications.globalSearch(cs.getGlobalSearch());
    } else {
      customerSpecification = CustomerSpecifications.searchCustomer(
              cs.getName(), cs.getEmail(), cs.getPhoneNumber(), cs.getCity(), cs.getState());
    }

    Pageable pageable = PageRequest.of(
            cs.getPage() - 1, // Convert to 0-based indexing
            cs.getSize(),
            Sort.by(Sort.Direction.fromString(cs.getSortDir()), cs.getSortBy()));

    Page<Customer> customersPage = customerRepository.findAll(customerSpecification, pageable);

    return customersPage.map(customer -> CustomerResponseDto.builder()
            .id(customer.getId())
            .name(customer.getName())
            .email(customer.getEmail())
            .phoneNumber(customer.getPhoneNumber())
            .address(customer.getAddress())
            .city(customer.getCity())
            .state(customer.getState())
            .createdAt(customer.getCreatedAt().toString())
            .build());
  }

  public Customer updateCustomer(Long id, UpdateCustomerDto dto) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

    if (dto.getName() != null) {
      customer.setName(dto.getName());
    }
    if (dto.getEmail() != null) {
      customer.setEmail(dto.getEmail());
    }
    if (dto.getPhoneNumber() != null) {
      customer.setPhoneNumber(dto.getPhoneNumber());
    }
    if (dto.getAddress() != null) {
      customer.setAddress(dto.getAddress());
    }
    if (dto.getCity() != null) {
      customer.setCity(dto.getCity());
    }
    if (dto.getState() != null) {
      customer.setState(dto.getState());
    }

    return customerRepository.save(customer);
  }

  public void deleteCustomer(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    customerRepository.deleteById(id);
  }

  public CustomerResponseDto getCustomerById(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    
    return CustomerResponseDto.builder()
        .id(customer.getId())
        .name(customer.getName())
        .email(customer.getEmail())
        .phoneNumber(customer.getPhoneNumber())
        .address(customer.getAddress())
        .city(customer.getCity())
        .state(customer.getState())
        .createdAt(customer.getCreatedAt().toString())
        .build();
  }
}
