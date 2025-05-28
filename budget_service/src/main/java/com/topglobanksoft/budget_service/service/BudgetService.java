package com.topglobanksoft.budget_service.service;

import com.topglobanksoft.budget_service.dto.BudgetCreateDTO;
import com.topglobanksoft.budget_service.dto.BudgetDTO;
import com.topglobanksoft.budget_service.dto.BudgetUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {

    //Defines the interface to manage budgets
    BudgetDTO createBudget(String userId, BudgetCreateDTO budgetCreateDTO);
    BudgetDTO getBudgetByIdAndUser(Long budgetId, String userId);
    List<BudgetDTO> getBudgetsByUserAndPeriod(String userId, Integer year, Integer month);
    Page<BudgetDTO> getAllBudgetsByUser(String userId, Pageable pageable);
    BudgetDTO updateBudgetInfo(Long budgetId, String userId, BudgetUpdateDTO budgetUpdateDTO);
    void deleteBudget(Long budgetId, String userId);

    //Updates the SpentAmount of an specific budget asociated to a user
    void updateBudgetSpentAmount(String userId, Long categoryId, int year, int month, BigDecimal spentAmount);
}