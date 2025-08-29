package com.example.wmsnew.warehouse.service;

import com.example.wmsnew.warehouse.dto.*;
import com.example.wmsnew.warehouse.entity.*;
import com.example.wmsnew.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncWarehouseCreationService {

    private final WarehouseCreationJobRepository jobRepository;
    private final WarehouseRepository warehouseRepository;
    private final StandardSizesRepository standardSizesRepository;

    @Transactional
    public AsyncWarehouseCreationResponse initiateAsyncWarehouseCreation(WarehouseRequest request) {
        log.info("Initiating async warehouse creation for: {}", request.getWarehouseName());
        
        // Calculate estimated total locations
        int estimatedTotalLocations = request.getLocationGroups().stream()
                .mapToInt(group -> group.getRows() * group.getRacksPerRow() * 
                         group.getShelvesPerRack() * group.getBinsPerShelf())
                .sum();

        // Create warehouse entity first (without locations)
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse = warehouseRepository.save(warehouse);

        // Create job tracking record
        WarehouseCreationJob job = WarehouseCreationJob.builder()
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getWarehouseName())
                .jobStatus(WarehouseCreationJob.JobStatus.PENDING)
                .startTime(LocalDateTime.now())
                .totalLocations(estimatedTotalLocations)
                .createdLocations(0)
                .build();
        
        job = jobRepository.save(job);
        
        // Trigger async location creation
        createLocationsAsync(job.getId(), warehouse, request);
        
        return AsyncWarehouseCreationResponse.builder()
                .jobId(job.getId())
                .warehouseId(warehouse.getId())
                .message("Warehouse creation started. Locations are being generated in the background.")
                .estimatedTotalLocations(estimatedTotalLocations)
                .build();
    }

    @Async
    @Transactional
    public void createLocationsAsync(Long jobId, Warehouse warehouse, WarehouseRequest request) {
        log.info("Starting async location creation for warehouse: {} (Job ID: {})", 
                warehouse.getWarehouseName(), jobId);
        
        Optional<WarehouseCreationJob> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            log.error("Job not found: {}", jobId);
            return;
        }

        WarehouseCreationJob job = jobOpt.get();
        
        try {
            // Update job status to IN_PROGRESS
            job.setJobStatus(WarehouseCreationJob.JobStatus.IN_PROGRESS);
            jobRepository.save(job);
            
            int totalCreated = 0;
            
            // Generate locations for each location group
            for (LocationGroupRequest group : request.getLocationGroups()) {
                StandardSizes size = standardSizesRepository
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

                                warehouse.addLocation(location);
                                totalCreated++;
                                
                                // Update progress periodically (every 100 locations)
                                if (totalCreated % 100 == 0) {
                                    job.setCreatedLocations(totalCreated);
                                    jobRepository.save(job);
                                    log.info("Progress update: {}/{} locations created for warehouse: {}", 
                                            totalCreated, job.getTotalLocations(), warehouse.getWarehouseName());
                                }
                            }
                        }
                    }
                }
            }

            // Save all locations
            warehouseRepository.save(warehouse);
            
            // Update job as completed
            job.setCreatedLocations(totalCreated);
            job.setJobStatus(WarehouseCreationJob.JobStatus.COMPLETED);
            job.setEndTime(LocalDateTime.now());
            jobRepository.save(job);
            
            log.info("Successfully completed warehouse creation: {} with {} locations (Job ID: {})", 
                    warehouse.getWarehouseName(), totalCreated, jobId);
            
        } catch (Exception e) {
            log.error("Failed to create locations for warehouse: {} (Job ID: {})", 
                    warehouse.getWarehouseName(), jobId, e);
            
            // Update job as failed
            job.setJobStatus(WarehouseCreationJob.JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setEndTime(LocalDateTime.now());
            jobRepository.save(job);
        }
    }

    public WarehouseCreationStatusResponse getCreationStatus(Long jobId) {
        WarehouseCreationJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        
        return WarehouseCreationStatusResponse.fromEntity(job);
    }
}