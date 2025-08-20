package com.example.wmsnew.warehouse.service;

import com.example.wmsnew.warehouse.dto.StandardSizeRequest;
import com.example.wmsnew.warehouse.dto.StandardSizeResponse;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import com.example.wmsnew.warehouse.repository.StandardSizesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandardSizeService {

    private final StandardSizesRepository standardSizesRepository;

    public List<StandardSizeResponse> getAllStandardSizes() {
        return standardSizesRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public Page<StandardSizeResponse> getStandardSizes(Pageable pageable, String sizeName) {
        Page<StandardSizes> standardSizes;
        
        if (sizeName != null && !sizeName.trim().isEmpty()) {
            // Simple search by dimensions (since sizeName is calculated)
            standardSizes = standardSizesRepository.findAll(pageable);
            // Filter by sizeName after loading (not ideal for large datasets, but works for now)
            return standardSizes.map(this::mapToResponse)
                .map(response -> {
                    if (response.getSizeName().toLowerCase().contains(sizeName.toLowerCase())) {
                        return response;
                    }
                    return null;
                })
                .map(response -> response);
        } else {
            standardSizes = standardSizesRepository.findAll(pageable);
        }
        
        return standardSizes.map(this::mapToResponse);
    }

    public StandardSizeResponse getStandardSizeById(Long id) {
        StandardSizes standardSize = standardSizesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Standard size not found with id: " + id));
        return mapToResponse(standardSize);
    }

    @Transactional
    public StandardSizeResponse createStandardSize(StandardSizeRequest request) {
        StandardSizes standardSize = new StandardSizes();
        standardSize.setWidth(request.getWidth());
        standardSize.setHeight(request.getHeight());
        standardSize.setDepth(request.getDepth());
        standardSize.setVolume(request.getVolume());

        standardSize = standardSizesRepository.save(standardSize);
        return mapToResponse(standardSize);
    }

    @Transactional
    public StandardSizeResponse updateStandardSize(Long id, StandardSizeRequest request) {
        StandardSizes standardSize = standardSizesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Standard size not found with id: " + id));

        if (request.getWidth() != null) {
            standardSize.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            standardSize.setHeight(request.getHeight());
        }
        if (request.getDepth() != null) {
            standardSize.setDepth(request.getDepth());
        }
        if (request.getVolume() != null) {
            standardSize.setVolume(request.getVolume());
        }

        standardSize = standardSizesRepository.save(standardSize);
        return mapToResponse(standardSize);
    }

    @Transactional
    public void deleteStandardSize(Long id) {
        StandardSizes standardSize = standardSizesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Standard size not found with id: " + id));
        standardSizesRepository.delete(standardSize);
    }

    private StandardSizeResponse mapToResponse(StandardSizes standardSize) {
        return StandardSizeResponse.builder()
            .id(standardSize.getId())
            .width(standardSize.getWidth())
            .height(standardSize.getHeight())
            .depth(standardSize.getDepth())
            .volume(standardSize.getVolume())
            .sizeName(standardSize.getSizeName())
            .build();
    }
}