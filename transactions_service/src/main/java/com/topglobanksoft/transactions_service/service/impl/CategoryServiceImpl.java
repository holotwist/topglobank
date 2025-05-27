package com.topglobanksoft.transactions_service.service.impl;

import com.topglobanksoft.transactions_service.dto.category.CategoryCreateUpdateDTO;
import com.topglobanksoft.transactions_service.dto.category.CategoryDTO;
import com.topglobanksoft.transactions_service.entity.Category;
import com.topglobanksoft.transactions_service.exception.CategoryInUseException;
import com.topglobanksoft.transactions_service.exception.ResourceNotFoundException;
import com.topglobanksoft.transactions_service.mapper.CategoryMapper;
import com.topglobanksoft.transactions_service.repository.CategoryRepository;
import com.topglobanksoft.transactions_service.repository.TransactionRepository;
import com.topglobanksoft.transactions_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Service implementation for category management operations
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository; // To check if category is in use
    private final CategoryMapper categoryMapper;
    /**
     * Creates a new category after checking for name uniqueness
     */
    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryCreateUpdateDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new DataIntegrityViolationException("Category with name '" + categoryDTO.getName() + "' already exists.");
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }
    /**
     * Retrieves a category by ID
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return categoryMapper.toDto(category);
    }
    /**
     * Retrieves all categories as a list
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves paginated categories
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }
    /**
     * Updates an existing category after validation
     */
    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryCreateUpdateDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // Check if new name conflicts with another existing category
        categoryRepository.findByName(categoryDTO.getName()).ifPresent(existingCategory -> {
            if (!existingCategory.getCategoryId().equals(categoryId)) {
                throw new DataIntegrityViolationException("Another category with name '" + categoryDTO.getName() + "' already exists.");
            }
        });

        categoryMapper.updateEntityFromDto(categoryDTO, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }
    /**
     * Deletes a category if not in use by transactions
     */
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        if (transactionRepository.existsByCategoryCategoryId(categoryId)) {
            throw new CategoryInUseException("Category '" + category.getName() + "' cannot be deleted as it is associated with existing transactions.");
        }
        categoryRepository.delete(category);
    }
}