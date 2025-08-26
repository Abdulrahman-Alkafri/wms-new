package com.example.wmsnew.product;

import com.example.wmsnew.product.dto.ProductBaseSearchCriteria;
import com.example.wmsnew.product.dto.ProductCreateRequest;
import com.example.wmsnew.product.dto.ProductResponse;
import com.example.wmsnew.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
    ProductResponse response = productService.createProduct(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getAllProducts(
      @ModelAttribute ProductBaseSearchCriteria criteria) {
    log.info("=== ProductController.getAllProducts ENTRY ===");
    log.info("Received criteria: {}", criteria);
    try {
      if (criteria == null) {
        log.info("Creating default criteria");
        criteria = ProductBaseSearchCriteria.builder().build();
      }
      Page<ProductResponse> products = productService.findAllProducts(criteria);
      log.info("Successfully retrieved {} products", products.getTotalElements());
      return ResponseEntity.ok(products);
    } catch (Exception e) {
      log.error("Error in getAllProducts", e);
      throw e;
    }
  }

  @GetMapping("/search")
  public ResponseEntity<Page<ProductResponse>> findAllProducts(
      @ModelAttribute ProductBaseSearchCriteria criteria) {
    Page<ProductResponse> products = productService.findAllProducts(criteria);
    return ResponseEntity.ok(products);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
    ProductResponse product = productService.getProductById(id);
    return ResponseEntity.ok(product);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Integer id, @Valid @RequestBody ProductUpdateRequest request) {
    ProductResponse updated = productService.updateProduct(id, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}