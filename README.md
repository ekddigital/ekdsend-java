# EKDSend Java SDK

Official Java SDK for the EKDSend API - Send emails, SMS, and voice calls with ease.

[![Maven Central](https://img.shields.io/maven-central/v/com.ekddigital/ekdsend-java)](https://search.maven.org/artifact/com.ekddigital/ekdsend-java)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Requirements

- Java 17 or later
- Maven or Gradle

## Installation

### Maven

```xml
<dependency>
    <groupId>com.ekddigital</groupId>
    <artifactId>ekdsend-java</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.ekddigital:ekdsend-java:1.1.0'
```

## Quick Start

```java
import com.ekddigital.ekdsend.EKDSend;
import com.ekddigital.ekdsend.model.Email;
import com.ekddigital.ekdsend.api.EmailsApi.SendEmailRequest;

public class Example {
    public static void main(String[] args) {
        // Initialize the client
        EKDSend client = EKDSend.builder("ek_live_xxxxxxxxxxxxx").build();
        
        try {
            // Send an email
            Email email = client.emails().send(SendEmailRequest.builder()
                .from("hello@yourdomain.com")
                .to("user@example.com")
                .subject("Hello from EKDSend!")
                .html("<h1>Welcome!</h1><p>Thanks for signing up.</p>")
                .build());
            
            System.out.println("Email sent! ID: " + email.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Configuration

```java
import com.ekddigital.ekdsend.EKDSend;
import java.time.Duration;

EKDSend client = EKDSend.builder("ek_live_xxxxxxxxxxxxx")
    .baseUrl("https://es.ekddigital.com/v1")  // Custom base URL
    .timeout(Duration.ofSeconds(60))         // Request timeout
    .maxRetries(5)                           // Max retry attempts
    .debug(true)                             // Enable debug logging
    .build();
```

## Email API

### Send an Email

```java
import com.ekddigital.ekdsend.api.EmailsApi.SendEmailRequest;
import com.ekddigital.ekdsend.model.Email;
import java.util.List;
import java.util.Map;

// Simple email
Email email = client.emails().send(SendEmailRequest.builder()
    .from("hello@yourdomain.com")
    .to("user@example.com")
    .subject("Hello!")
    .html("<h1>Welcome!</h1>")
    .build());

// With all options
Email email = client.emails().send(SendEmailRequest.builder()
    .from("hello@yourdomain.com")
    .to(List.of("user@example.com", "another@example.com"))
    .subject("Welcome to our platform")
    .html("<h1>Welcome!</h1><p>Thanks for joining.</p>")
    .text("Welcome! Thanks for joining.")
    .cc(List.of("cc@example.com"))
    .bcc(List.of("bcc@example.com"))
    .replyTo("support@yourdomain.com")
    .tags(List.of("welcome", "onboarding"))
    .metadata(Map.of("user_id", "12345"))
    .build());
```

### Get Email Status

```java
Email email = client.emails().get("email_xxxxxxxxxxxxx");
System.out.println("Status: " + email.getStatus());
```

### List Emails

```java
import com.ekddigital.ekdsend.api.EmailsApi.ListEmailsRequest;
import com.ekddigital.ekdsend.api.EmailsApi.EmailsListResponse;

EmailsListResponse response = client.emails().list(ListEmailsRequest.builder()
    .limit(20)
    .offset(0)
    .status("delivered")
    .build());

for (Email email : response.getData()) {
    System.out.println(email.getId() + ": " + email.getSubject());
}

// Pagination
while (response.hasMore()) {
    response = client.emails().list(ListEmailsRequest.builder()
        .limit(20)
        .offset(response.getOffset() + 20)
        .build());
    // Process emails...
}
```

### Cancel Scheduled Email

```java
Email cancelled = client.emails().cancel("email_xxxxxxxxxxxxx");
```

## SMS API

### Send SMS

```java
import com.ekddigital.ekdsend.api.SmsApi.SendSmsRequest;
import com.ekddigital.ekdsend.model.Sms;

Sms sms = client.sms().send(SendSmsRequest.builder()
    .to("+1234567890")
    .from("+0987654321")
    .message("Your verification code is 123456")
    .build());

System.out.println("SMS sent! ID: " + sms.getId());
```

### Get SMS Status

```java
Sms sms = client.sms().get("sms_xxxxxxxxxxxxx");
System.out.println("Status: " + sms.getStatus());
System.out.println("Segments: " + sms.getSegments());
```

### List SMS Messages

```java
import com.ekddigital.ekdsend.api.SmsApi.ListSmsRequest;
import com.ekddigital.ekdsend.api.SmsApi.SmsListResponse;

SmsListResponse response = client.sms().list(ListSmsRequest.builder()
    .limit(50)
    .build());

for (Sms sms : response.getData()) {
    System.out.println(sms.getId() + ": " + sms.getMessage());
}
```

## Voice API

### Create a Voice Call

```java
import com.ekddigital.ekdsend.api.VoiceApi.CreateCallRequest;
import com.ekddigital.ekdsend.model.VoiceCall;

// Text-to-speech call
VoiceCall call = client.calls().create(CreateCallRequest.builder()
    .to("+1234567890")
    .from("+0987654321")
    .ttsMessage("Hello! This is an automated call from EKDSend.")
    .voice("female")
    .language("en-US")
    .record(true)
    .build());

// Pre-recorded audio call
VoiceCall call = client.calls().create(CreateCallRequest.builder()
    .to("+1234567890")
    .from("+0987654321")
    .audioUrl("https://example.com/audio.mp3")
    .build());
```

### Get Call Status

```java
VoiceCall call = client.calls().get("call_xxxxxxxxxxxxx");
System.out.println("Status: " + call.getStatus());
System.out.println("Duration: " + call.getDuration() + " seconds");
```

### Hangup Active Call

```java
VoiceCall call = client.calls().hangup("call_xxxxxxxxxxxxx");
```

### Get Call Recording

```java
import com.ekddigital.ekdsend.model.Recording;

Recording recording = client.calls().getRecording("call_xxxxxxxxxxxxx");
System.out.println("Recording URL: " + recording.getUrl());
```

## Error Handling

```java
import com.ekddigital.ekdsend.exception.*;

try {
    Email email = client.emails().send(SendEmailRequest.builder()
        .from("hello@yourdomain.com")
        .to("user@example.com")
        .subject("Hello!")
        .html("<h1>Welcome!</h1>")
        .build());
} catch (AuthenticationException e) {
    System.err.println("Invalid API key: " + e.getMessage());
} catch (ValidationException e) {
    System.err.println("Validation error: " + e.getMessage());
    System.err.println("Details: " + e.getErrors());
} catch (RateLimitException e) {
    System.err.println("Rate limited. Retry after: " + e.getRetryAfter() + " seconds");
} catch (NotFoundException e) {
    System.err.println("Resource not found: " + e.getMessage());
} catch (EKDSendException e) {
    System.err.println("API error: " + e.getMessage());
    System.err.println("Status code: " + e.getStatusCode());
    System.err.println("Request ID: " + e.getRequestId());
}
```

## Exception Types

| Exception | HTTP Code | Description |
|-----------|-----------|-------------|
| `AuthenticationException` | 401 | Invalid or missing API key |
| `ValidationException` | 400 | Invalid request parameters |
| `RateLimitException` | 429 | Rate limit exceeded |
| `NotFoundException` | 404 | Resource not found |
| `EKDSendException` | Various | General API error |

## Thread Safety

The `EKDSend` client is thread-safe and can be shared across multiple threads. It's recommended to create a single instance and reuse it throughout your application.

```java
// Create once, reuse everywhere
public class EKDSendService {
    private static final EKDSend client = EKDSend.builder(
        System.getenv("EKDSEND_API_KEY")
    ).build();
    
    public static EKDSend getClient() {
        return client;
    }
}
```

## Spring Boot Integration

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EKDSendConfig {
    
    @Bean
    public EKDSend ekdSendClient() {
        return EKDSend.builder(System.getenv("EKDSEND_API_KEY"))
            .debug(Boolean.parseBoolean(System.getenv("EKDSEND_DEBUG")))
            .build();
    }
}
```

```java
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private final EKDSend ekdSend;
    
    public NotificationService(EKDSend ekdSend) {
        this.ekdSend = ekdSend;
    }
    
    public void sendWelcomeEmail(String email, String name) throws EKDSendException {
        ekdSend.emails().send(SendEmailRequest.builder()
            .from("hello@yourdomain.com")
            .to(email)
            .subject("Welcome, " + name + "!")
            .html("<h1>Welcome to our platform!</h1>")
            .build());
    }
}
```

## License

MIT License - see [LICENSE](LICENSE) for details.
