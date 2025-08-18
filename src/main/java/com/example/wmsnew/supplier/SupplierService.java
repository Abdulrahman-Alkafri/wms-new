package com.example.wmsnew.supplier;

import com.example.wmsnew.supplier.dto.CreateSupplierDto;
import com.example.wmsnew.supplier.dto.SupplierBaseSearchCriteria;
import com.example.wmsnew.supplier.dto.SupplierResponseDto;
import com.example.wmsnew.supplier.dto.UpdateSupplierDto;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.supplier.repository.SupplierSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierService {

  private final SupplierRepository supplierRepository;

  // Create Supplier
  public SupplierResponseDto createSupplier(CreateSupplierDto dto) {
    Supplier supplier =
        Supplier.builder()
            .name(dto.getName())
            .contactPerson(dto.getContactPerson())
            .email(dto.getEmail())
            .phoneNumber(dto.getPhoneNumber())
            .address(dto.getAddress())
            .city(dto.getCity())
            .state(dto.getState())
            .build();

    supplier = supplierRepository.save(supplier);

    return SupplierResponseDto.builder()
        .id(supplier.getId())
        .name(supplier.getName())
        .contactPerson(supplier.getContactPerson())
        .email(supplier.getEmail())
        .phoneNumber(supplier.getPhoneNumber())
        .address(supplier.getAddress())
        .city(supplier.getCity())
        .state(supplier.getState())
        .build();
  }

  // Search Suppliers
  public Page<SupplierResponseDto> findAllSuppliers(SupplierBaseSearchCriteria cs) {
    Specification<Supplier> spec =
        SupplierSpecifications.searchSupplier(
            cs.getName(),
            cs.getContactPerson(),
            cs.getEmail(),
            cs.getPhoneNumber(),
            cs.getCity(),
            cs.getState());

    Pageable pageable =
        PageRequest.of(
            cs.getPage(),
            cs.getSize(),
            Sort.by(Sort.Direction.fromString(cs.getSortDir()), cs.getSortBy()));

    Page<Supplier> suppliers = supplierRepository.findAll(spec, pageable);

    return suppliers.map(
        supplier ->
            SupplierResponseDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phoneNumber(supplier.getPhoneNumber())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .state(supplier.getState())
                .build());
  }

  public Supplier updateSupplier(Long id, UpdateSupplierDto dto) {
    Supplier supplier =
        supplierRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

    if (dto.getName() != null) supplier.setName(dto.getName());
    if (dto.getContactPerson() != null) supplier.setContactPerson(dto.getContactPerson());
    if (dto.getEmail() != null) supplier.setEmail(dto.getEmail());
    if (dto.getPhoneNumber() != null) supplier.setPhoneNumber(dto.getPhoneNumber());
    if (dto.getAddress() != null) supplier.setAddress(dto.getAddress());
    if (dto.getCity() != null) supplier.setCity(dto.getCity());
    if (dto.getState() != null) supplier.setState(dto.getState());

    Supplier updated = supplierRepository.save(supplier);
    return updated;
  }

  // 🔹 Delete Supplier
  public void deleteSupplier(Long id) {
    if (!supplierRepository.existsById(id)) {
      throw new RuntimeException("Supplier not found with id: " + id);
    }
    supplierRepository.deleteById(id);
  }
}
