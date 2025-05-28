package com.topglobanksoft.budget_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Defines a personalized exception
@ResponseStatus(HttpStatus.FORBIDDEN)
public class BudgetAccessException extends RuntimeException {
    public BudgetAccessException(String message) {
        super(message);
    }
}