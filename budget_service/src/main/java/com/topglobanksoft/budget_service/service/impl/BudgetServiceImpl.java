package com.topglobanksoft.budget_service.service.impl;

import com.topglobanksoft.budget_service.dto.BudgetCreateDTO;
import com.topglobanksoft.budget_service.dto.BudgetDTO;
import com.topglobanksoft.budget_service.dto.BudgetUpdateDTO;
import com.topglobanksoft.budget_service.entity.Budget;
// import com.topglobanksoft.budget_service.exception.BudgetAccessException; // Not used
import com.topglobanksoft.budget_service.exception.BudgetAlreadyExistsException;
import com.topglobanksoft.budget_service.exception.OptimisticLockingFailureException;
import com.topglobanksoft.budget_service.exception.ResourceNotFoundException;
import com.topglobanksoft.budget_service.mapper.BudgetMapper;
import com.topglobanksoft.budget_service.repository.BudgetRepository;
import com.topglobanksoft.budget_service.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public BudgetDTO createBudget(String userId, BudgetCreateDTO budgetCreateDTO) { // Changed
        if (budgetRepository.existsByUserIdAndCategoryIdAndYearAndMonth(
                userId, budgetCreateDTO.getCategoryId(), budgetCreateDTO.getYear(), budgetCreateDTO.getMonth())) {
            throw new BudgetAlreadyExistsException(
                    String.format("A budget for user %s, category %d, year %d, month %d already exists.", // %s for String userId
                            userId, budgetCreateDTO.getCategoryId(), budgetCreateDTO.getYear(), budgetCreateDTO.getMonth()));
        }
        Budget budget = budgetMapper.toEntity(budgetCreateDTO);
        budget.setUserId(userId);
        budget.setAmountSpent(BigDecimal.ZERO);

        try {
            Budget savedBudget = budgetRepository.save(budget);
            log.info("Budget created: ID={}, UserID={}, CategoryID={}, Year={}, Month={}",
                    savedBudget.getBudgetId(), userId, savedBudget.getCategoryId(), savedBudget.getYear(), savedBudget.getMonth());
            return budgetMapper.toDto(savedBudget);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation during budget creation for user {}: {}", userId, e.getMessage());
            throw new BudgetAlreadyExistsException("Failed to create budget due to a conflict. It might already exist.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetDTO getBudgetByIdAndUser(Long budgetId, String userId) { // Changed
        Budget budget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget not found with ID %d for user %s", budgetId, userId))); // %s for String userId
        return budgetMapper.toDto(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> getBudgetsByUserAndPeriod(String userId, Integer year, Integer month) { // Changed
        List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonthOrderByCategoryId(userId, year, month);
        return budgets.stream().map(budgetMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BudgetDTO> getAllBudgetsByUser(String userId, Pageable pageable) { // Changed
        return budgetRepository.findByUserIdOrderByYearDescMonthDescNameAsc(userId, pageable)
                .map(budgetMapper::toDto);
    }

    @Override
    @Transactional
    public BudgetDTO updateBudgetInfo(Long budgetId, String userId, BudgetUpdateDTO budgetUpdateDTO) { // Changed
        Budget budget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget not found with ID %d for user %s to update.", budgetId, userId))); // %s

        budgetMapper.updateEntityFromDto(budgetUpdateDTO, budget);

        try {
            Budget updatedBudget = budgetRepository.save(budget);
            log.info("Budget info updated: ID={}, UserID={}", budgetId, userId);
            return budgetMapper.toDto(updatedBudget);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure while updating budget info for ID {}: {}", budgetId, e.getMessage());
            throw new OptimisticLockingFailureException("Budget was updated by another process. Please try again.", e);
        }
    }

    @Override
    @Transactional
    public void deleteBudget(Long budgetId, String userId) { // Changed
        Budget budget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget not found with ID %d for user %s to delete.", budgetId, userId))); // %s
        budgetRepository.delete(budget);
        log.info("Budget deleted: ID={}, UserID={}", budgetId, userId);
    }

    @Override
    @Transactional
    public void updateBudgetSpentAmount(String userId, Long categoryId, int year, int month, BigDecimal spentAmount) { // Changed
        log.info("Attempting to update spent amount for budget: User={}, Category={}, Year={}, Month={}, AmountToAdd={}",
                userId, categoryId, year, month, spentAmount);

        Budget budget = budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(userId, categoryId, year, month)
                .orElseGet(() -> {
                    log.warn("No existing budget found for User={}, Category={}, Year={}, Month={}. Spending will not be tracked.",
                            userId, categoryId, year, month);
                    return null;
                });

        if (budget != null) {
            try {
                budget.setAmountSpent(budget.getAmountSpent().add(spentAmount));
                budgetRepository.save(budget);
                log.info("Successfully updated spent amount for budget ID {}. New spent amount: {}", budget.getBudgetId(), budget.getAmountSpent());
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic locking failure while updating spent amount for budget ID {}. User={}, Category={}, Year={}, Month={}. Error: {}",
                        budget.getBudgetId(), userId, categoryId, year, month, e.getMessage());
                throw new OptimisticLockingFailureException(
                        String.format("Conflict updating budget for User %s, Category %d, Period %d-%d. Please retry.", userId, categoryId, year, month), e); // %s
            }
        }
    }
}