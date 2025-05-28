package com.topglobanksoft.transactions_service.service;

import com.topglobanksoft.transactions_service.dto.category.CategoryCreateUpdateDTO;
import com.topglobanksoft.transactions_service.dto.category.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
/**
 * Service for managing transaction categories
 */
public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateUpdateDTO categoryDTO);
    CategoryDTO getCategoryById(Long categoryId);
    List<CategoryDTO> getAllCategories(); // For selection lists, etc.
    Page<CategoryDTO> getAllCategories(Pageable pageable); // Paginated for admin UI
    CategoryDTO updateCategory(Long categoryId, CategoryCreateUpdateDTO categoryDTO);
    void deleteCategory(Long categoryId);
}