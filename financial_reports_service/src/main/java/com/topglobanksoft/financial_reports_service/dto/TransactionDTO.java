package com.topglobanksoft.financial_reports_service.dto;

// import com.fasterxml.jackson.annotation.JsonProperty; // Not strictly needed if names match
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Defines a DTO to transfer data in the app
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private String description;
    private String userId;
    private String sourceUserId;
    private String destinationUserId;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private CategoryInfoDTO category;
    private Long relatedTransactionId;

    //Represents basic information associated to a transaction
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfoDTO {
        private Long categoryId;
        private String name;
    }
}