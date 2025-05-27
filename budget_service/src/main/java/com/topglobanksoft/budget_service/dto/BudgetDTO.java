package com.topglobanksoft.budget_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long budgetId;
    private String name;
    private BigDecimal initialAmount;
    private BigDecimal amountSpent;
    private BigDecimal amountRemaining;
    private String userId;
    private Long categoryId;
    private Integer year;
    private Integer month;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private Long version;

    public BigDecimal getAmountRemaining() {
        if (initialAmount != null && amountSpent != null) {
            return initialAmount.subtract(amountSpent);
        }
        return initialAmount != null ? initialAmount : BigDecimal.ZERO;
    }
}