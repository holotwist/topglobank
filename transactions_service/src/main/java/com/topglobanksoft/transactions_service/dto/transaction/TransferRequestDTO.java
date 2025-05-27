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
public class TransferRequestDTO {
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;

    // Assuming destinationUserId can be looked up if needed, or comes from frontend validation
    // For simplicity, we'll assume bank_accounts_service holds userId for each account
    // and we can query that if needed, or that the Kafka event for balance update is enough.
    // @NotNull(message = "Destination user ID is required")
    // private Long destinationUserId; // Could also be destinationUserEmail to be looked up

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    private String description;
    private Long categoryId; // Optional, for the sender's perspective
}