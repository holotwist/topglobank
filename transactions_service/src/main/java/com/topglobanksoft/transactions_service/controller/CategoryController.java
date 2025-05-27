package com.topglobanksoft.transactions_service.controller;

import com.topglobanksoft.transactions_service.dto.category.CategoryCreateUpdateDTO;
import com.topglobanksoft.transactions_service.dto.category.CategoryDTO;
import com.topglobanksoft.transactions_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions/categories")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // REMOVE from class level, for more granular security
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Apply to admin-specific methods
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryCreateUpdateDTO categoryDTO) {
        CategoryDTO newCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apply to admin-specific methods
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/all") // Endpoint to get all categories without pagination
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Allow USER and ADMIN to access this
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesList() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Paginated list, typically for admin UI
    public ResponseEntity<Page<CategoryDTO>> getAllCategoriesPaginated(@PageableDefault(size = 10) Pageable pageable) {
        Page<CategoryDTO> categoriesPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categoriesPage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apply to admin-specific methods
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreateUpdateDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apply to admin-specific methods
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}