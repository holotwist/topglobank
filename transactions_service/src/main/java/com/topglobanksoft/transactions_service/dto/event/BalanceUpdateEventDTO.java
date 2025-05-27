package com.topglobanksoft.transactions_service.dto.event;

import com.topglobanksoft.transactions_service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateEventDTO {
    private Long transactionId;
    private String userId;
    private Long accountId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private String description;
    private Long categoryId;
}