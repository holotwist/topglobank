package com.topglobanksoft.bank_accounts_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


//Base account data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String bankName;
    private String accountNumber;
    private String accountType;
    private String userId;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}