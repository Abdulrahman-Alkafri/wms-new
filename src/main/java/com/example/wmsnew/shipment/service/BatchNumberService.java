package com.example.wmsnew.shipment.service;

import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.shipment.repository.ShipmentItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BatchNumberService {
    
    private final ShipmentItemsRepository shipmentItemsRepository;
    private final InventoryRepository inventoryRepository;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public String generateUniqueBatchNumber(Integer shipmentId, Integer itemId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String baseBatchNumber = String.format("SHP-%d-ITM-%d-%s", shipmentId, itemId, timestamp);
        
        // Ensure uniqueness by checking against existing batches
        String batchNumber = baseBatchNumber;
        int suffix = 1;
        
        while (batchNumberExists(batchNumber)) {
            batchNumber = baseBatchNumber + "-" + suffix;
            suffix++;
        }
        
        return batchNumber;
    }
    
    private boolean batchNumberExists(String batchNumber) {
        return shipmentItemsRepository.existsByBatchNumber(batchNumber) ||
               inventoryRepository.existsByBatchNumber(batchNumber);
    }
}