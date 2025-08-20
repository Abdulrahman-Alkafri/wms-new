package com.example.wmsnew.warehouse.service;

// WarehouseService.java

import com.example.wmsnew.Exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.example.wmsnew.warehouse.dto.*;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import com.example.wmsnew.warehouse.entity.Warehouse;
import com.example.wmsnew.warehouse.repository.StandardSizesRepository;
import com.example.wmsnew.warehouse.repository.WarehouseRepository;
import com.example.wmsnew.warehouse.repository.WarehouseSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final StandardSizesRepository standardSizesRepository;

  @Transactional
  public WarehouseResponse createWarehouse(WarehouseRequest request) {
    Warehouse warehouse = new Warehouse();
    warehouse.setWarehouseName(request.getWarehouseName());

    List<String> locationCodes = new ArrayList<>();

    for (LocationGroupRequest group : request.getLocationGroups()) {
      StandardSizes size =
          standardSizesRepository
              .findById(group.getStandardSizeId())
              .orElseThrow(() -> new RuntimeException("Standard size not found"));

      for (int row = 1; row <= group.getRows(); row++) {
        for (int rack = 1; rack <= group.getRacksPerRow(); rack++) {
          for (int shelf = 1; shelf <= group.getShelvesPerRack(); shelf++) {
            for (int bin = 1; bin <= group.getBinsPerShelf(); bin++) {
              Location location = new Location();
              location.setWarehouse(warehouse);
              location.setStandardSize(size);
              location.setAsile("A" + row);
              location.setRack("R" + rack);
              location.setShelf("S" + shelf);
              location.setBin("B" + bin);

              // Generate unique location code
              String code = "R" + row + "-RK" + rack + "-S" + shelf + "-B" + bin;
              location.setLocationCode(code);
              locationCodes.add(code);

              warehouse.addLocation(location);
            }
          }
        }
      }
    }

    warehouse = warehouseRepository.save(warehouse);

    WarehouseResponse response = new WarehouseResponse();
    response.setId(warehouse.getId());
    response.setWarehouseName(warehouse.getWarehouseName());
    response.setLocationCodes(locationCodes);

    return response;
  }

  public Page<AllWarehouseResponse> findAllWarehouses(WarehouseSearchCriteria criteria) {
    Specification<Warehouse> spec =
        WarehouseSpecifications.searchWarehouse(criteria.getWarehouseName());

    Pageable pageable =
        PageRequest.of(
            criteria.getPage(),
            criteria.getSize(),
            Sort.by(Sort.Direction.fromString(criteria.getSortDir()), criteria.getSortBy()));

    Page<Warehouse> warehouses = warehouseRepository.findAll(spec, pageable);

    return warehouses.map(
        warehouse ->
            AllWarehouseResponse.builder()
                .id(warehouse.getId())
                .warehouseName(warehouse.getWarehouseName())
                .totalLocations(warehouse.getLocations().size())
                .build());
  }

  public WarehouseResponse getWarehouseById(Long warehouseId) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

    List<String> locationCodes = warehouse.getLocations().stream()
        .map(Location::getLocationCode)
        .toList();

    WarehouseResponse response = new WarehouseResponse();
    response.setId(warehouse.getId());
    response.setWarehouseName(warehouse.getWarehouseName());
    response.setLocationCodes(locationCodes);

    return response;
  }

  @Transactional
  public WarehouseResponse updateWarehouse(Long warehouseId, UpdateWarehouseRequest request) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

    if (request.getWarehouseName() != null && !request.getWarehouseName().isBlank()) {
      warehouse.setWarehouseName(request.getWarehouseName());
    }

    List<String> newLocationCodes = new ArrayList<>();

    if (request.getLocationGroups() != null && !request.getLocationGroups().isEmpty()) {
      for (var group : request.getLocationGroups()) {
        StandardSizes size =
            standardSizesRepository
                .findById(group.getStandardSizeId())
                .orElseThrow(() -> new RuntimeException("Standard size not found"));

        for (int row = 1; row <= group.getRows(); row++) {
          for (int rack = 1; rack <= group.getRacksPerRow(); rack++) {
            for (int shelf = 1; shelf <= group.getShelvesPerRack(); shelf++) {
              for (int bin = 1; bin <= group.getBinsPerShelf(); bin++) {
                String code = "R" + row + "-RK" + rack + "-S" + shelf + "-B" + bin;

                // ✅ Prevent duplicates
                boolean exists =
                    warehouse.getLocations().stream()
                        .anyMatch(loc -> loc.getLocationCode().equals(code));
                if (exists) continue;

                Location location = new Location();
                location.setWarehouse(warehouse);
                location.setStandardSize(size);
                location.setAsile("A" + row);
                location.setRack("R" + rack);
                location.setShelf("S" + shelf);
                location.setBin("B" + bin);
                location.setLocationCode(code);

                warehouse.addLocation(location);
                newLocationCodes.add(code);
              }
            }
          }
        }
      }
    }

    warehouse = warehouseRepository.save(warehouse);

    WarehouseResponse response = new WarehouseResponse();
    response.setId(warehouse.getId());
    response.setWarehouseName(warehouse.getWarehouseName());
    response.setLocationCodes(newLocationCodes);

    return response;
  }

  @Transactional
  public void deleteWarehouse(Long warehouseId) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));

    warehouseRepository.delete(warehouse);
  }
}
