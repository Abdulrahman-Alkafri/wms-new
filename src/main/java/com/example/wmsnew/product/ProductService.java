package com.example.wmsnew.product;

import com.example.wmsnew.product.dto.*;
import com.example.wmsnew.product.entity.*;
import com.example.wmsnew.product.repository.*;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import com.example.wmsnew.warehouse.repository.StandardSizesRepository;
import com.example.wmsnew.inventory.InventoryService;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final StandardSizesRepository standardSizesRepository;
  private final InventoryService inventoryService;
  private final LocationRepository locationRepository;

  @Transactional
  public ProductResponse createProduct(ProductCreateRequest request) {
    Category category =
        categoryRepository
            .findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

    Product product =
        Product.builder()
            .productName(request.getProductName())
            .brandName(request.getBrandName())
            .description(request.getDescription())
            .price(request.getPrice())
            .category(category)
            .build();

    // link sizes
    if (request.getSizes() != null) {
      for (ProductSizeDto sizeReq : request.getSizes()) {
        StandardSizes standardSize =
            standardSizesRepository
                .findById(sizeReq.getStandardSizeId())
                .orElseThrow(() -> new RuntimeException("Standard size not found"));

        ProductStandardSizes pss = new ProductStandardSizes();
        pss.setProduct(product);
        pss.setStandardSizes(standardSize);
        pss.setMaxQuantity(sizeReq.getMaxQuantity());

        product.addProductStandardSize(pss);
      }
    }

    // Save the product first to get the ID
    product = productRepository.save(product);
    
    // Generate initial inventory entries for the new product
    generateInitialInventory(product);

    return mapToResponse(product);
  }

  public Page<ProductResponse> findAllProducts(ProductBaseSearchCriteria cs) {
    log.info("ProductService.findAllProducts called with criteria: {}", cs);
    
    // Handle null criteria
    if (cs == null) {
      log.info("Creating default criteria");
      cs = ProductBaseSearchCriteria.builder().build();
    }
    log.info("Criteria after null check: {}", cs);
    
    try {
      Specification<Product> spec =
          ProductSpecifications.searchProduct(
              cs.getProductName(), cs.getBrandName(), cs.getCategoryName());

      // Handle null values with defaults
      Integer page = cs.getPage() != null ? cs.getPage() : 0; // Products use 0-based indexing
      Integer size = cs.getSize() != null ? cs.getSize() : 10;
      String sortBy = cs.getSortBy() != null ? cs.getSortBy() : "id";
      String sortDir = cs.getSortDir() != null ? cs.getSortDir() : "asc";

      log.info("Pagination: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

      Pageable pageable =
          PageRequest.of(
              page,
              size,
              Sort.by(Sort.Direction.fromString(sortDir), sortBy));

      log.info("About to call productRepository.findAll...");
      Page<Product> products = productRepository.findAll(spec, pageable);
      log.info("Repository call completed. Found {} products", products.getTotalElements());

      return products.map(product -> {
        // Fetch complete product data with relationships
        Product completeProduct = productRepository.findByIdWithDetails(product.getId())
            .orElse(product); // fallback to original if fetch fails
        return mapToResponse(completeProduct);
      });
    } catch (Exception e) {
      log.error("Error in findAllProducts", e);
      throw e;
    }
  }

  private ProductResponse mapToResponse(Product product) {
    ProductResponse response = new ProductResponse();
    response.setId(product.getId());
    response.setProductName(product.getProductName());
    response.setBrandName(product.getBrandName());
    response.setDescription(product.getDescription());
    response.setPrice(product.getPrice());
    
    // Safe handling of category (might be lazy loaded)
    if (product.getCategory() != null) {
      response.setCategoryId(product.getCategory().getId());
      response.setCategoryName(product.getCategory().getName());
    }
    
    response.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
    
    // Safe handling of product sizes (might be lazy loaded)
    if (product.getProductStandardSizes() != null && !product.getProductStandardSizes().isEmpty()) {
      List<ProductResponse.ProductSizeResponse> sizeResponses = product.getProductStandardSizes().stream()
        .map(pss -> {
          ProductResponse.ProductSizeResponse sizeResponse = new ProductResponse.ProductSizeResponse();
          sizeResponse.setStandardSizeId(pss.getStandardSizes().getId());
          sizeResponse.setStandardSizeName(pss.getStandardSizes().getSizeName());
          sizeResponse.setMaxQuantity(pss.getMaxQuantity());
          return sizeResponse;
        }).toList();
      response.setSizes(sizeResponses);
    }
    
    return response;
  }

  public ProductResponse getProductById(Integer id) {
    Product product = productRepository.findByIdWithDetails(id)
        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    return mapToResponse(product);
  }

  @Transactional
  public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

    // Update basic fields
    if (request.getProductName() != null) {
      product.setProductName(request.getProductName());
    }
    if (request.getBrandName() != null) {
      product.setBrandName(request.getBrandName());
    }
    if (request.getDescription() != null) {
      product.setDescription(request.getDescription());
    }
    if (request.getPrice() != null) {
      product.setPrice(request.getPrice());
    }
    if (request.getCategoryId() != null) {
      Category category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new RuntimeException("Category not found"));
      product.setCategory(category);
    }

    // Update sizes
    if (request.getSizes() != null) {
      // Clear existing sizes
      product.getProductStandardSizes().clear();
      
      // Add new sizes
      for (ProductSizeDto sizeReq : request.getSizes()) {
        StandardSizes standardSize = standardSizesRepository
            .findById(sizeReq.getStandardSizeId())
            .orElseThrow(() -> new RuntimeException("Standard size not found"));

        ProductStandardSizes pss = new ProductStandardSizes();
        pss.setProduct(product);
        pss.setStandardSizes(standardSize);
        pss.setMaxQuantity(sizeReq.getMaxQuantity());

        product.addProductStandardSize(pss);
      }
    }

    productRepository.save(product);
    return mapToResponse(product);
  }

  @Transactional
  public void deleteProduct(Integer id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    productRepository.delete(product);
  }

  private void generateInitialInventory(Product product) {
    log.info("Generating initial inventory for product: {}", product.getProductName());
    
    // Get available locations (limit to first few locations to avoid creating too many records)
    List<Location> availableLocations = locationRepository.findAll()
        .stream()
        .limit(5) // Only create inventory in first 5 locations
        .toList();
    
    if (availableLocations.isEmpty()) {
      log.warn("No locations found to create inventory for product: {}", product.getProductName());
      return;
    }

    // Generate a simple batch number for initial inventory
    String batchNumber = "INIT-" + product.getId() + "-001";
    
    // Create initial inventory entries with zero quantity in a few locations
    for (Location location : availableLocations) {
      try {
        inventoryService.addInventory(
            product,
            location,
            0, // Start with zero inventory
            batchNumber,
            null, // No manufacturing date for initial inventory
            null  // No expiry date for initial inventory
        );
        
        log.info("Created initial inventory for product {} at location {}", 
                product.getProductName(), location.getLocationCode());
      } catch (Exception e) {
        log.error("Failed to create inventory for product {} at location {}: {}", 
                product.getProductName(), location.getLocationCode(), e.getMessage());
        // Continue with other locations even if one fails
      }
    }
  }
}
