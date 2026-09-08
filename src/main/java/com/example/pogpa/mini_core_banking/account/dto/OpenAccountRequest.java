package com.example.pogpa.mini_core_banking.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;
public record OpenAccountRequest(
        @NotNull(message = "customerId is required")
        UUID customerId,
        @NotNull(message = "accountTypeId is required")
        Long accountTypeId,
        Long branchId,
        @Pattern( regexp = "^[A-Z]{3}$", message = "currencyCode must be 3 uppercase letters" )
        String currencyCode
) {
}
