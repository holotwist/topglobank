package com.topglobanksoft.transactions_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when account lacks sufficient funds for transaction
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Or CONFLICT / UNPROCESSABLE_ENTITY
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}