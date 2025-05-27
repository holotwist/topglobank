package com.topglobanksoft.statistics_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Used when external service communication fails (HTTP 503)
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String message) {
        super(message);
    }

    public ServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}