package com.example.wmsnew.product;

import com.example.wmsnew.product.dto.*;
import com.example.wmsnew.product.entity.*;
import com.example.wmsnew.product.repository.*;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import com.example.wmsnew.warehouse.repository.StandardSizesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final StandardSizesRepository standardSizesRepository;

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

    productRepository.save(product);

    return mapToResponse(product);
  }

  public Page<ProductResponse> findAllProducts(ProductBaseSearchCriteria cs) {
    Specification<Product> spec =
        ProductSpecifications.searchProduct(
            cs.getProductName(), cs.getBrandName(), cs.getCategoryName());

    Pageable pageable =
        PageRequest.of(
            cs.getPage(),
            cs.getSize(),
            Sort.by(Sort.Direction.fromString(cs.getSortDir()), cs.getSortBy()));

    Page<Product> products = productRepository.findAll(spec, pageable);

    return products.map(
        product -> {
          ProductResponse response = new ProductResponse();
          response.setId(product.getId());
          response.setProductName(product.getProductName());
          response.setBrandName(product.getBrandName());
          response.setDescription(product.getDescription());
          response.setPrice(product.getPrice());
          response.setCategoryName(product.getCategory().getName());
          return response; // return inside the lambda
        });
  }

  private ProductResponse mapToResponse(Product product) {
    ProductResponse response = new ProductResponse();
    response.setId(product.getId());
    response.setProductName(product.getProductName());
    response.setBrandName(product.getBrandName());
    response.setDescription(product.getDescription());
    response.setPrice(product.getPrice());
    response.setCategoryId(product.getCategory().getId());
    response.setCategoryName(product.getCategory().getName());
    response.setCreatedAt(product.getCreatedAt().toString());
    
    // Map product sizes
    List<ProductResponse.ProductSizeResponse> sizeResponses = product.getProductStandardSizes().stream()
      .map(pss -> {
        ProductResponse.ProductSizeResponse sizeResponse = new ProductResponse.ProductSizeResponse();
        sizeResponse.setStandardSizeId(pss.getStandardSizes().getId());
        sizeResponse.setStandardSizeName(pss.getStandardSizes().getSizeName());
        sizeResponse.setMaxQuantity(pss.getMaxQuantity());
        return sizeResponse;
      }).toList();
    response.setSizes(sizeResponses);
    
    return response;
  }

  public ProductResponse getProductById(Integer id) {
    Product product = productRepository.findById(id)
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
}
