package com.topglobanksoft.financial_reports_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// It's good practice to assign a status
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // Or BAD_GATEWAY if it's specifically about an upstream service
public class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String message) {
        super(message);
    }

    public ServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}