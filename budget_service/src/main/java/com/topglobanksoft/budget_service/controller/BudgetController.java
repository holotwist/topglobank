package com.topglobanksoft.budget_service.controller;

import com.topglobanksoft.budget_service.dto.BudgetCreateDTO;
import com.topglobanksoft.budget_service.dto.BudgetDTO;
import com.topglobanksoft.budget_service.dto.BudgetUpdateDTO;
import com.topglobanksoft.budget_service.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('USER')")
public class BudgetController {

    private final BudgetService budgetService;

    private String getUserIdFromToken(Jwt jwt) { // Changed return type to String
        String userId = jwt.getSubject();
        if (userId == null || userId.isBlank()) {
            log.error("Keycloak 'sub' claim (userId) is missing or blank in JWT: {}", jwt.getClaims());
            throw new IllegalArgumentException("User ID claim ('sub') not found or invalid in JWT");
        }
        return userId;
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody BudgetCreateDTO budgetCreateDTO,
                                                  @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        BudgetDTO newBudget = budgetService.createBudget(userId, budgetCreateDTO);
        return new ResponseEntity<>(newBudget, HttpStatus.CREATED);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> getMyBudgetById(@PathVariable Long budgetId,
                                                     @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        BudgetDTO budget = budgetService.getBudgetByIdAndUser(budgetId, userId);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/period")
    public ResponseEntity<List<BudgetDTO>> getMyBudgetsByPeriod(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        List<BudgetDTO> budgets = budgetService.getBudgetsByUserAndPeriod(userId, year, month);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping
    public ResponseEntity<Page<BudgetDTO>> getAllMyBudgets(
            @PageableDefault(size = 10, sort = {"year", "month"}) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        Page<BudgetDTO> budgetsPage = budgetService.getAllBudgetsByUser(userId, pageable);
        return ResponseEntity.ok(budgetsPage);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> updateMyBudgetInfo(@PathVariable Long budgetId,
                                                        @Valid @RequestBody BudgetUpdateDTO budgetUpdateDTO,
                                                        @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        BudgetDTO updatedBudget = budgetService.updateBudgetInfo(budgetId, userId, budgetUpdateDTO);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteMyBudget(@PathVariable Long budgetId,
                                               @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        budgetService.deleteBudget(budgetId, userId);
        return ResponseEntity.noContent().build();
    }
}