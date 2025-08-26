package com.example.wmsnew.shipment.service;

import com.example.wmsnew.Exceptions.warehouseExceptions.LocationCapacityExceededException;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.product.entity.ProductStandardSizes;
import com.example.wmsnew.product.repository.ProductStandardSizesRepository;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationOptimizationService {
    
    private final LocationRepository locationRepository;
    private final ProductStandardSizesRepository productStandardSizesRepository;
    
    public Location findOptimalLocation(Product product, Integer quantity) {
        // Get product standard sizes
        List<ProductStandardSizes> productSizes = productStandardSizesRepository.findByProduct(product);
        
        if (productSizes.isEmpty()) {
            throw new LocationCapacityExceededException("No standard sizes defined for product: " + product.getProductName());
        }
        
        // Find compatible locations
        for (ProductStandardSizes productSize : productSizes) {
            List<Location> compatibleLocations = locationRepository
                    .findByStandardSizeAndAvailableCapacity(
                            productSize.getStandardSizes(), 
                            quantity.doubleValue()
                    );
            
            if (!compatibleLocations.isEmpty()) {
                // Return the first available location (can be enhanced with distance optimization)
                return compatibleLocations.get(0);
            }
        }
        
        throw new LocationCapacityExceededException(
                String.format("No available location found for product %s with quantity %d", 
                        product.getProductName(), quantity)
        );
    }
    
    public boolean canLocationAccommodate(Location location, Integer quantity) {
        if (location.getStandardSize() == null) {
            return false;
        }
        
        double requiredCapacity = quantity.doubleValue();
        double availableCapacity = location.getStandardSize().getVolume() - location.getCurrentLoad();
        
        return availableCapacity >= requiredCapacity;
    }
}