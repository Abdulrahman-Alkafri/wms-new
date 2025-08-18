package com.example.wmsnew.customer;

import com.example.wmsnew.customer.dtos.CreateCustomerDto;
import com.example.wmsnew.customer.dtos.CustomerBaseSearchCriteria;
import com.example.wmsnew.customer.dtos.CustomerResponseDto;
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

  public void createCustomer(CreateCustomerDto dto) {
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
  }

  public Page<CustomerResponseDto> findAllCustomers(CustomerBaseSearchCriteria cs) {
    Specification<Customer> customerSpecification =
            CustomerSpecifications.searchCustomer(
                    cs.getName(), cs.getEmail(), cs.getPhoneNumber(), cs.getCity(), cs.getState());

    Pageable pageable = PageRequest.of(
            cs.getPage(),
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
            .build());
  }
}
