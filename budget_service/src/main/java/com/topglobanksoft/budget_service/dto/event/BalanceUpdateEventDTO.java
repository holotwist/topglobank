package com.topglobanksoft.budget_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//DTO class, represents a balance update event
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateEventDTO {
    private Long transactionId;
    private String userId;
    private Long accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private Long categoryId;
    private String description;
}