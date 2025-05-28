package com.topglobanksoft.statistics_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Transport object for API data exchange
 */
public class UserInfoDTO {
    private String idUser;
    private String fullName;
    private BigDecimal balance;
}