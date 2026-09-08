package com.example.pogpa.mini_core_banking.audit.context;

import com.example.pogpa.mini_core_banking.audit.metadata.AuditMetadata;

public class AuditFunctionContext {
    private static final ThreadLocal<AuditMetadata> CURRENT =
            new ThreadLocal<>();

    private AuditFunctionContext() {
    }

    public static void set(AuditMetadata metadata) {
        CURRENT.set(metadata);
    }

    public static AuditMetadata get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
