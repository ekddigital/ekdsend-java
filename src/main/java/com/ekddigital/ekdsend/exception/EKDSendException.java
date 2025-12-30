package com.ekddigital.ekdsend.exception;

/**
 * Base exception for EKDSend API errors
 */
public class EKDSendException extends Exception {

    private final int statusCode;
    private final String errorCode;
    private final String requestId;

    public EKDSendException(String message, int statusCode, String errorCode, String requestId) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EKDSendException{");
        sb.append("message='").append(getMessage()).append("'");
        sb.append(", statusCode=").append(statusCode);
        sb.append(", errorCode='").append(errorCode).append("'");
        if (requestId != null) {
            sb.append(", requestId='").append(requestId).append("'");
        }
        sb.append("}");
        return sb.toString();
    }
}
