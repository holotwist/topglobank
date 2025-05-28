package com.topglobanksoft.bank_accounts_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Launches an exception when a user doesnt have enough permissions to modificate an account
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountAccessException extends RuntimeException {
    public AccountAccessException(String message) {
        super(message);
    }
}