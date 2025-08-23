package com.example.wmsnew.product;

import com.example.wmsnew.product.dto.ProductBaseSearchCriteria;
import com.example.wmsnew.product.dto.ProductCreateRequest;
import com.example.wmsnew.product.dto.ProductResponse;
import com.example.wmsnew.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
    ProductResponse response = productService.createProduct(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
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