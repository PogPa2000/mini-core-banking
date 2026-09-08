package com.example.pogpa.mini_core_banking.audit.metadata;

public record AuditChange(
        String functionKey,
        String action,
        String entityType,
        String entityId,
        Object oldData,
        Object newData
) {
}
