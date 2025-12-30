package com.ekddigital.ekdsend.exception;

import java.util.Map;

/**
 * Exception for validation errors (400)
 */
public class ValidationException extends EKDSendException {

    private final Map<String, Object> errors;

    public ValidationException(String message, Map<String, Object> errors, String requestId) {
        super(message, 400, "VALIDATION_ERROR", requestId);
        this.errors = errors;
    }

    public Map<String, Object> getErrors() {
        return errors;
    }
}
