package com.ekddigital.ekdsend;

import com.ekddigital.ekdsend.api.EmailsApi;
import com.ekddigital.ekdsend.api.SmsApi;
import com.ekddigital.ekdsend.api.VoiceApi;
import com.ekddigital.ekdsend.exception.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * EKDSend API Client
 * <p>
 * The main entry point for interacting with the EKDSend API.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * EKDSend client = EKDSend.builder("ek_live_xxxxxxxxxxxxx").build();
 * 
 * Email email = client.emails().send(SendEmailRequest.builder()
 *         .from("hello@yourdomain.com")
 *         .to(List.of("user@example.com"))
 *         .subject("Hello!")
 *         .html("<h1>Welcome!</h1>")
 *         .build());
 * </pre>
 */
public class EKDSend {

    public static final String VERSION = "1.1.0";
    public static final String DEFAULT_BASE_URL = "https://es.ekddigital.com/v1";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    public static final int DEFAULT_MAX_RETRIES = 3;

    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final boolean debug;
    private final int maxRetries;

    private final EmailsApi emails;
    private final SmsApi sms;
    private final VoiceApi calls;

    private EKDSend(Builder builder) {
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.debug = builder.debug;
        this.maxRetries = builder.maxRetries;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(builder.timeout)
                .readTimeout(builder.timeout)
                .writeTimeout(builder.timeout)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .header("User-Agent", "ekdsend-java/" + VERSION)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.emails = new EmailsApi(this);
        this.sms = new SmsApi(this);
        this.calls = new VoiceApi(this);
    }

    /**
     * Create a new builder for EKDSend client
     *
     * @param apiKey Your EKDSend API key
     * @return Builder instance
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    /**
     * Get the Email API
     */
    public EmailsApi emails() {
        return emails;
    }

    /**
     * Get the SMS API
     */
    public SmsApi sms() {
        return sms;
    }

    /**
     * Get the Voice API
     */
    public VoiceApi calls() {
        return calls;
    }

    /**
     * Make an HTTP request to the API
     */
    public <T> T request(String method, String path, Object body, Class<T> responseType) throws EKDSendException {
        String url = baseUrl + path;

        if (debug) {
            System.out.println("[EKDSend] " + method + " " + path);
        }

        RequestBody requestBody = null;
        if (body != null) {
            try {
                String json = objectMapper.writeValueAsString(body);
                if (debug) {
                    System.out.println("[EKDSend] Request: " + json);
                }
                requestBody = RequestBody.create(json, MediaType.parse("application/json"));
            } catch (Exception e) {
                throw new EKDSendException("Failed to serialize request body", 0, "SERIALIZATION_ERROR", null);
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .build();

        EKDSendException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                String requestId = response.header("x-request-id");

                if (debug) {
                    System.out.println("[EKDSend] Response (" + response.code() + "): " + responseBody);
                }

                if (!response.isSuccessful()) {
                    lastException = handleError(response.code(), responseBody, requestId);

                    // Don't retry auth or validation errors
                    if (lastException instanceof AuthenticationException ||
                            lastException instanceof ValidationException) {
                        throw lastException;
                    }

                    // Retry on rate limit or server errors
                    if (attempt < maxRetries && (response.code() == 429 || response.code() >= 500)) {
                        Thread.sleep((long) Math.pow(2, attempt) * 1000);
                        continue;
                    }

                    throw lastException;
                }

                if (responseType != null && !responseBody.isEmpty()) {
                    return objectMapper.readValue(responseBody, responseType);
                }
                return null;

            } catch (IOException | InterruptedException e) {
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new EKDSendException("Request interrupted", 0, "INTERRUPTED", null);
                    }
                    continue;
                }
                throw new EKDSendException("Request failed: " + e.getMessage(), 0, "CONNECTION_ERROR", null);
            }
        }

        throw lastException != null ? lastException
                : new EKDSendException("Request failed after retries", 0, "UNKNOWN_ERROR", null);
    }

    private EKDSendException handleError(int statusCode, String body, String requestId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> errorResponse = objectMapper.readValue(body, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> error = (Map<String, Object>) errorResponse.get("error");

            String message = error != null ? (String) error.get("message") : "API request failed";
            String code = error != null ? (String) error.get("code") : "UNKNOWN_ERROR";

            return switch (statusCode) {
                case 400 -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = error != null ? (Map<String, Object>) error.get("details") : Map.of();
                    yield new ValidationException(message, details, requestId);
                }
                case 401 -> new AuthenticationException(message, requestId);
                case 404 -> new NotFoundException(message, code, requestId);
                case 429 -> {
                    int retryAfter = error != null && error.get("retry_after") != null
                            ? ((Number) error.get("retry_after")).intValue()
                            : 60;
                    yield new RateLimitException(message, retryAfter, requestId);
                }
                default -> new EKDSendException(message, statusCode, code, requestId);
            };
        } catch (Exception e) {
            return new EKDSendException("API request failed", statusCode, "UNKNOWN_ERROR", requestId);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Builder for EKDSend client
     */
    public static class Builder {
        private final String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private Duration timeout = DEFAULT_TIMEOUT;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private boolean debug = false;

        private Builder(String apiKey) {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalArgumentException("API key is required");
            }
            if (!apiKey.startsWith("ek_live_") && !apiKey.startsWith("ek_test_")) {
                throw new IllegalArgumentException(
                        "Invalid API key format. Must start with 'ek_live_' or 'ek_test_'");
            }
            this.apiKey = apiKey;
        }

        /**
         * Set custom base URL
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl.replaceAll("/$", "");
            return this;
        }

        /**
         * Set request timeout
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Set maximum retry attempts
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Enable debug logging
         */
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * Build the EKDSend client
         */
        public EKDSend build() {
            return new EKDSend(this);
        }
    }
}
