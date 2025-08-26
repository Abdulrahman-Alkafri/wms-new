package com.example.wmsnew.supplier;

import com.example.wmsnew.supplier.dto.CreateSupplierDto;
import com.example.wmsnew.supplier.dto.SupplierBaseSearchCriteria;
import com.example.wmsnew.supplier.dto.SupplierResponseDto;
import com.example.wmsnew.supplier.dto.UpdateSupplierDto;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.supplier.repository.SupplierSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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
        .createdAt(supplier.getCreatedAt().toString())
        .build();
  }

  // Search Suppliers
  public Page<SupplierResponseDto> findAllSuppliers(SupplierBaseSearchCriteria cs) {
    log.info("SupplierService.findAllSuppliers called with criteria: {}", cs);
    // Handle null criteria
    if (cs == null) {
      log.info("Creating default criteria");
      cs = SupplierBaseSearchCriteria.builder().build();
    }
    log.info("Criteria after null check: {}", cs);
    
    try {
      log.info("Creating specification...");
      Specification<Supplier> spec;
      
      if (cs.getGlobalSearch() != null && !cs.getGlobalSearch().isEmpty()) {
        log.info("Using global search: {}", cs.getGlobalSearch());
        spec = SupplierSpecifications.globalSearch(cs.getGlobalSearch());
      } else {
        log.info("Using detailed search");
        spec = SupplierSpecifications.searchSupplier(
                cs.getName(),
                cs.getContactPerson(),
                cs.getEmail(),
                cs.getPhoneNumber(),
                cs.getCity(),
                cs.getState());
      }
      log.info("Specification created successfully");

      // Handle null values with defaults
      Integer page = cs.getPage() != null ? cs.getPage() : 1;
      Integer size = cs.getSize() != null ? cs.getSize() : 10;
      String sortBy = cs.getSortBy() != null ? cs.getSortBy() : "id";
      String sortDir = cs.getSortDir() != null ? cs.getSortDir() : "asc";
      
      log.info("Pagination: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

      Pageable pageable =
          PageRequest.of(
              page - 1, // Convert to 0-based indexing
              size,
              Sort.by(Sort.Direction.fromString(sortDir), sortBy));

      log.info("Created pageable: {}", pageable);
      log.info("About to call supplierRepository.findAll...");
      Page<Supplier> suppliers = supplierRepository.findAll(spec, pageable);
      log.info("Repository call completed. Found {} suppliers", suppliers.getTotalElements());

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
                .createdAt(supplier.getCreatedAt().toString())
                .build());
    } catch (Exception e) {
      log.error("Error in findAllSuppliers", e);
      throw e;
    }
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

  public SupplierResponseDto getSupplierById(Long id) {
    Supplier supplier = supplierRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    
    return SupplierResponseDto.builder()
        .id(supplier.getId())
        .name(supplier.getName())
        .contactPerson(supplier.getContactPerson())
        .email(supplier.getEmail())
        .phoneNumber(supplier.getPhoneNumber())
        .address(supplier.getAddress())
        .city(supplier.getCity())
        .state(supplier.getState())
        .createdAt(supplier.getCreatedAt().toString())
        .build();
  }
}
