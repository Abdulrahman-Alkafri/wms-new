package com.example.wmsnew.product;

import com.example.wmsnew.Exceptions.productExceptions.CategoryNotFoundException;
import com.example.wmsnew.product.dto.CategoryResponseDto;
import com.example.wmsnew.product.dto.CategorySearchCriteria;
import com.example.wmsnew.product.dto.CreateCategoryDto;
import com.example.wmsnew.product.dto.UpdateCategoryDto;
import com.example.wmsnew.product.entity.Category;
import com.example.wmsnew.product.repository.CategoryRepository;
import com.example.wmsnew.product.repository.CategorySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Page<CategoryResponseDto> getAllCategories(CategorySearchCriteria searchCriteria) {
        Sort sort = Sort.by(
            searchCriteria.getSortDir().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            searchCriteria.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(
            searchCriteria.getPage() - 1,
            searchCriteria.getSize(),
            sort
        );

        Specification<Category> spec = CategorySpecifications.withCriteria(searchCriteria);
        
        return categoryRepository.findAll(spec, pageable)
            .map(this::mapToResponseDto);
    }

    public List<CategoryResponseDto> getAllCategoriesSimple() {
        return categoryRepository.findAll()
            .stream()
            .map(this::mapToResponseDto)
            .toList();
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return mapToResponseDto(category);
    }

    public CategoryResponseDto createCategory(CreateCategoryDto createDto) {
        Category category = Category.builder()
            .name(createDto.getName())
            .build();
        
        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    public CategoryResponseDto updateCategory(Long id, UpdateCategoryDto updateDto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        category.setName(updateDto.getName());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        categoryRepository.delete(category);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
            .id(category.getId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}