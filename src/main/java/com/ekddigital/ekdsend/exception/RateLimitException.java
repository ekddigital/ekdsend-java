package com.ekddigital.ekdsend.exception;

/**
 * Exception for rate limit errors (429)
 */
public class RateLimitException extends EKDSendException {

    private final int retryAfter;

    public RateLimitException(String message, int retryAfter, String requestId) {
        super(message, 429, "RATE_LIMIT_EXCEEDED", requestId);
        this.retryAfter = retryAfter;
    }

    /**
     * Get the number of seconds to wait before retrying
     */
    public int getRetryAfter() {
        return retryAfter;
    }
}
