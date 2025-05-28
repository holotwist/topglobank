package com.topglobanksoft.budget_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCreateDTO {

    //Defines validation restrictions
    @NotBlank(message = "Budget name cannot be empty")
    @Size(max = 100, message = "Budget name must be less than 100 characters")
    private String name;

    @NotNull(message = "Initial amount is required")
    @DecimalMin(value = "0.01", message = "Initial amount must be positive")
    private BigDecimal initialAmount;

    @NotNull(message = "Category ID is required")
    private Long categoryId; // FK to Category in transactions_service

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Invalid year")
    @Max(value = 2100, message = "Invalid year") // Sensible upper limit
    private Integer year;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Invalid month")
    @Max(value = 12, message = "Invalid month")
    private Integer month;
}