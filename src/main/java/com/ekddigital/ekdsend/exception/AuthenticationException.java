package com.ekddigital.ekdsend.exception;

/**
 * Exception for authentication errors (401)
 */
public class AuthenticationException extends EKDSendException {

    public AuthenticationException(String message, String requestId) {
        super(message, 401, "AUTHENTICATION_ERROR", requestId);
    }
}
