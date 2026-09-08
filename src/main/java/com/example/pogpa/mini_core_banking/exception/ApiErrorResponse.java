package com.example.pogpa.mini_core_banking.exception;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message
) {
}