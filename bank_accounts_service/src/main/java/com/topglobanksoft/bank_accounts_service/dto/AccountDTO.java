package com.topglobanksoft.bank_accounts_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String bankName;
    private String accountNumber; // FUTURE: we need to consider masking this in DTOs for non-owners
    private String accountType;
    private Long userId;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}