package com.topglobanksoft.transactions_service.dto.transaction;

import com.topglobanksoft.transactions_service.entity.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
/**
 * Transport object for API data exchange
 */
@Data
public class TransactionFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private TransactionType type;
    private Long categoryId;
    private Integer page;
    private Integer size;
}