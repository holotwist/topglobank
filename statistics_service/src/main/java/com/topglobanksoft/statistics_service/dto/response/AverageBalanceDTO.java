package com.topglobanksoft.statistics_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageBalanceDTO {
    private BigDecimal averageBalance;
    private long totalUsersConsidered;
}