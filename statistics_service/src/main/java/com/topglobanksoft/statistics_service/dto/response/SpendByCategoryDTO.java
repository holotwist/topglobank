package com.topglobanksoft.statistics_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Transport object for API data exchange
 */
public class SpendByCategoryDTO {
    private String categoryName;
    private BigDecimal totalSpent;
}