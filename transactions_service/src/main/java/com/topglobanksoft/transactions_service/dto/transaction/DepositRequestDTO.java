package com.topglobanksoft.transactions_service.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * Transport object for API data exchange
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDTO {
    @NotNull(message = "Destination account ID is required for deposit")
    private Long destinationAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    private String description;
    private Long categoryId; // Optional
}