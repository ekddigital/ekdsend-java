package com.ekddigital.ekdsend.exception;

/**
 * Exception for not found errors (404)
 */
public class NotFoundException extends EKDSendException {

    public NotFoundException(String message, String errorCode, String requestId) {
        super(message, 404, errorCode, requestId);
    }
}
