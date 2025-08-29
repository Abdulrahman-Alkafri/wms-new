package com.example.wmsnew.shipment.service;

import com.example.wmsnew.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentNumberGenerator {
    
    private final ShipmentRepository shipmentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicInteger sequenceCounter = new AtomicInteger(0);
    private String lastDate = "";
    
    /**
     * Generates a unique shipment number in the format: SHP-YYYYMMDD-XXXX
     * where YYYYMMDD is the current date and XXXX is a sequential number
     */
    public synchronized String generateUniqueShipmentNumber() {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        
        // Reset counter if it's a new day
        if (!currentDate.equals(lastDate)) {
            sequenceCounter.set(0);
            lastDate = currentDate;
            
            // Find the highest sequence number for today
            String prefix = "SHP-" + currentDate + "-";
            Integer maxSequence = shipmentRepository.findMaxSequenceForPrefix(prefix);
            if (maxSequence != null && maxSequence > 0) {
                sequenceCounter.set(maxSequence);
            }
        }
        
        String shipmentNumber;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;
        
        do {
            int sequence = sequenceCounter.incrementAndGet();
            shipmentNumber = String.format("SHP-%s-%04d", currentDate, sequence);
            attempts++;
            
            if (attempts > MAX_ATTEMPTS) {
                throw new RuntimeException("Unable to generate unique shipment number after " + MAX_ATTEMPTS + " attempts");
            }
        } while (shipmentRepository.existsByShipmentNumber(shipmentNumber));
        
        log.info("Generated shipment number: {}", shipmentNumber);
        return shipmentNumber;
    }
    
    /**
     * Validates if a shipment number follows the expected format
     */
    public boolean isValidShipmentNumber(String shipmentNumber) {
        if (shipmentNumber == null || shipmentNumber.trim().isEmpty()) {
            return false;
        }
        
        // Check if it matches the pattern SHP-YYYYMMDD-XXXX
        return shipmentNumber.matches("^SHP-\\d{8}-\\d{4}$");
    }
}
