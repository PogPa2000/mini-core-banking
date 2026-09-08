package com.example.pogpa.mini_core_banking.audit.service;

import com.example.pogpa.mini_core_banking.audit.context.AuditContext;
import com.example.pogpa.mini_core_banking.audit.entity.AuditLog;
import com.example.pogpa.mini_core_banking.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper;

    private final AuditContext auditContext;

    public void save(
            String functionKey,
            String entityType,
            String entityId,
            String action,
            Object oldValue,
            Object newValue) {

        AuditLog auditLog = new AuditLog();

        auditLog.setTraceId(
                auditContext.getTraceId()
        );

        auditLog.setActorType("USER");

        auditLog.setActorId(
                auditContext.getActorId()
        );

        auditLog.setFunctionKey(
                functionKey
        );

        auditLog.setAction(
                action
        );

        auditLog.setEntityType(
                entityType
        );

        auditLog.setEntityId(
                entityId
        );

        auditLog.setOldData(
                toJson(oldValue)
        );

        auditLog.setNewData(
                toJson(newValue)
        );

        auditLog.setIpAddress(
                auditContext.getIpAddress()
        );

        auditLog.setUserAgent(
                auditContext.getUserAgent()
        );

        auditLog.setCreatedAt(
                Instant.now()
        );

        auditLogRepository.save(auditLog);
    }

    private String toJson(Object value) {

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot convert audit data to JSON",
                    e
            );
        }
    }
}