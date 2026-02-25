// src/main/java/com/reservationapi/shared/web/ErrorResponse.java
package com.reservationapi.shared.web;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {

    Instant timestamp;
    int status;
    String errorCode;
    String message;
    String path;

    @Singular("fieldError")
    List<FieldErrorItem> fieldErrors;

    @Value
    @Builder
    public static class FieldErrorItem {
        String field;
        String message;
    }
}