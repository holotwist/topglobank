package com.topglobanksoft.transactions_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TransactionProcessingException extends RuntimeException {
    public TransactionProcessingException(String message) {
        super(message);
    }

    public TransactionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}