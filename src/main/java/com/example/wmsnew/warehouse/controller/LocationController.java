package com.example.wmsnew.warehouse.controller;

import com.example.wmsnew.warehouse.dto.LocationResponseDto;
import com.example.wmsnew.warehouse.dto.LocationSearchCriteria;
import com.example.wmsnew.warehouse.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  @GetMapping
  public Page<LocationResponseDto> getAllLocations(LocationSearchCriteria criteria) {
    return locationService.findAllLocations(criteria);
  }
}
