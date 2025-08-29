package com.example.wmsnew.warehouse.controller;

import com.example.wmsnew.warehouse.dto.StandardSizeRequest;
import com.example.wmsnew.warehouse.dto.StandardSizeResponse;
import com.example.wmsnew.warehouse.service.StandardSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standard-sizes")
@RequiredArgsConstructor
public class StandardSizeController {

    private final StandardSizeService standardSizeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<List<StandardSizeResponse>> getAllStandardSizes() {
        List<StandardSizeResponse> sizes = standardSizeService.getAllStandardSizes();
        return ResponseEntity.ok(sizes);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<Page<StandardSizeResponse>> getStandardSizes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String sizeName) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StandardSizeResponse> pagedSizes = standardSizeService.getStandardSizes(pageable, sizeName);
        return ResponseEntity.ok(pagedSizes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PICKER', 'STORER')")
    public ResponseEntity<StandardSizeResponse> getStandardSizeById(@PathVariable Long id) {
        StandardSizeResponse size = standardSizeService.getStandardSizeById(id);
        return ResponseEntity.ok(size);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardSizeResponse> createStandardSize(@RequestBody StandardSizeRequest request) {
        StandardSizeResponse response = standardSizeService.createStandardSize(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardSizeResponse> updateStandardSize(@PathVariable Long id, @RequestBody StandardSizeRequest request) {
        StandardSizeResponse response = standardSizeService.updateStandardSize(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStandardSize(@PathVariable Long id) {
        standardSizeService.deleteStandardSize(id);
        return ResponseEntity.noContent().build();
    }
}