package com.topglobanksoft.bank_accounts_service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDTO {
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    @Size(max = 50, message = "Account number must be less than 50 characters")
    private String accountNumber;

    @Size(max = 50, message = "Account type must be less than 50 characters")
    private String accountType;
}