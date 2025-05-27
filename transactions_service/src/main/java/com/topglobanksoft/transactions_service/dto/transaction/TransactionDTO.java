package com.topglobanksoft.transactions_service.dto.transaction;

import com.topglobanksoft.transactions_service.dto.category.CategoryDTO;
import com.topglobanksoft.transactions_service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Transport object for API data exchange
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private LocalDateTime date;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private String userId;
    private String sourceUserId;
    private String destinationUserId;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private CategoryDTO category;
    private Long relatedTransactionId;
}