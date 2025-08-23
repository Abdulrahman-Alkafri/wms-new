package com.example.wmsnew.inventory;

import com.example.wmsnew.Exceptions.inventoryException.InventoryNotFoundException;
import com.example.wmsnew.inventory.dto.InventoryResponseDto;
import com.example.wmsnew.inventory.dto.InventorySearchCriteria;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.inventory.repository.InventorySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Page<InventoryResponseDto> getAllInventory(InventorySearchCriteria searchCriteria) {
        Sort sort = Sort.by(
            searchCriteria.getSortDir().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            searchCriteria.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(
            searchCriteria.getPage() - 1,
            searchCriteria.getSize(),
            sort
        );

        Specification<Inventory> spec = InventorySpecifications.withCriteria(searchCriteria);
        
        return inventoryRepository.findAll(spec, pageable)
            .map(this::mapToResponseDto);
    }

    public InventoryResponseDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
            .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with id: " + id));
        return mapToResponseDto(inventory);
    }

    private InventoryResponseDto mapToResponseDto(Inventory inventory) {
        return InventoryResponseDto.builder()
            .id(inventory.getId())
            .productId(inventory.getProduct() != null ? inventory.getProduct().getId().longValue() : null)
            .productName(inventory.getProduct() != null ? inventory.getProduct().getProductName() : null)
            .brandName(inventory.getProduct() != null ? inventory.getProduct().getBrandName() : null)
            .categoryName(inventory.getProduct() != null && inventory.getProduct().getCategory() != null 
                ? inventory.getProduct().getCategory().getName() : null)
            .locationId(inventory.getLocation() != null ? inventory.getLocation().getId() : null)
            .locationCode(inventory.getLocation() != null ? inventory.getLocation().getLocationCode() : null)
            .warehouseName(inventory.getLocation() != null && inventory.getLocation().getWarehouse() != null 
                ? inventory.getLocation().getWarehouse().getWarehouseName() : null)
            .quantity(inventory.getQuantity())
            .batchNumber(inventory.getBatchNumber())
            .manufacturingDate(inventory.getManufacturingDate())
            .expiryDate(inventory.getExpiryDate())
            .createdAt(inventory.getCreatedAt())
            .updatedAt(inventory.getUpdatedAt())
            .build();
    }
}