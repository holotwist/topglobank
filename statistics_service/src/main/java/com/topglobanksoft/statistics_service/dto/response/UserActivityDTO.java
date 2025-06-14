package com.topglobanksoft.statistics_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Transport object for API data exchange
 */
public class UserActivityDTO {
    private String userId;
    private String userName;
    private Long transactionsQuantity;
}