package com.topglobanksoft.budget_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetUpdateDTO {
    @Size(max = 100, message = "Budget name must be less than 100 characters")
    private String name; // Optional update

    @DecimalMin(value = "0.01", message = "Initial amount must be positive if provided")
    private BigDecimal initialAmount; // Optional update

    // Category, year, month are usually part of the budget's identity,
    // so not typically updatable. If they need to change, it's often a new budget.
    // If they *are* updatable, add them here with validation.
    // private Long categoryId;
    // @Min(value = 2000, message = "Invalid year")
    // @Max(value = 2100, message = "Invalid year")
    // private Integer year;
    // @Min(value = 1, message = "Invalid month")
    // @Max(value = 12, message = "Invalid month")
    // private Integer month;
}