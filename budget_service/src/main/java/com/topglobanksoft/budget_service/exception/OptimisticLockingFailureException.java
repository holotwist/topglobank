package com.topglobanksoft.budget_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OptimisticLockingFailureException extends RuntimeException {
    public OptimisticLockingFailureException(String message) {
        super(message);
    }
    public OptimisticLockingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}