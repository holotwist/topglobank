package com.topglobanksoft.bank_accounts_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO Used to receive data when an account is being created
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDTO {
    @NotBlank(message = "Bank name cannot be empty")
    @Size(max = 100)
    private String bankName;

    @NotBlank(message = "Account number cannot be empty")
    @Size(max = 50)
    private String accountNumber;

    @NotBlank(message = "Account type cannot be empty")
    @Size(max = 50)
    private String accountType;
}