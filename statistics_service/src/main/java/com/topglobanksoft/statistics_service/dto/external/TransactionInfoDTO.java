package com.topglobanksoft.statistics_service.dto.external;

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
/**
 * Transport object for API data exchange
 */
public class TransactionInfoDTO {
    private Long transactionId;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private String userId;
    private Long categoryId;
    private String categoryName;
}