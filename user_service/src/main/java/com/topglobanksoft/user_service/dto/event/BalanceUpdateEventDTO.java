package com.topglobanksoft.user_service.dto.event;

import com.topglobanksoft.user_service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//DTO class used to transfer data into the app
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