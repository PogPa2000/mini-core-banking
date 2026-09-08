package com.example.pogpa.mini_core_banking.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UpdateAccountRequest(
        Long branchId,
        Long version
) {
}
