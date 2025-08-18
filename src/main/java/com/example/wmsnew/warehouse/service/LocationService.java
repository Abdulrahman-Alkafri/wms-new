package com.example.wmsnew.warehouse.service;

import com.example.wmsnew.warehouse.dto.LocationResponseDto;
import com.example.wmsnew.warehouse.dto.LocationSearchCriteria;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.repository.LocationRepository;
import com.example.wmsnew.warehouse.repository.LocationSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocationService {

  private LocationRepository locationRepository;

  public Page<LocationResponseDto> findAllLocations(LocationSearchCriteria criteria) {
    Specification<Location> spec =
        LocationSpecifications.searchLocation(
            criteria.getLocationCode(),
            criteria.getAisle(),
            criteria.getRack(),
            criteria.getShelf(),
            criteria.getBin(),
            criteria.getWarehouseName(),
            criteria.getStandardSizeName());

    Pageable pageable =
        PageRequest.of(
            criteria.getPage(),
            criteria.getSize(),
            Sort.by(Sort.Direction.fromString(criteria.getSortDir()), criteria.getSortBy()));

    return locationRepository
        .findAll(spec, pageable)
        .map(
            loc ->
                LocationResponseDto.builder()
                    .id(loc.getId())
                    .locationCode(loc.getLocationCode())
                    .aisle(loc.getAsile())
                    .rack(loc.getRack())
                    .shelf(loc.getShelf())
                    .bin(loc.getBin())
                    .standardSizeId(
                        loc.getStandardSize() != null
                            ? loc.getStandardSize().getId().intValue()
                            : null)
                    .standardSizeName(
                        loc.getStandardSize() != null ? loc.getStandardSize().getSizeName() : null)
                    .warehouseName(
                        loc.getWarehouse() != null ? loc.getWarehouse().getWarehouseName() : null)
                    .currentLoad(loc.getCurrentLoad())
                    .build());
  }
}
