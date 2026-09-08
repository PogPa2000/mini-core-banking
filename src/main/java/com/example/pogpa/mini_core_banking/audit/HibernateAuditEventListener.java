package com.example.pogpa.mini_core_banking.audit;

import com.example.pogpa.mini_core_banking.audit.context.AuditFunctionContext;
import com.example.pogpa.mini_core_banking.audit.context.AuditTransactionContext;
import com.example.pogpa.mini_core_banking.audit.entity.AuditLog;
import com.example.pogpa.mini_core_banking.audit.metadata.AuditChange;
import com.example.pogpa.mini_core_banking.audit.metadata.AuditMetadata;
import com.example.pogpa.mini_core_banking.enums.AuditAction;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class HibernateAuditEventListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    private final ObjectMapper objectMapper;
    private final ObjectProvider<AuditTransactionContext> auditTransactionContextProvider;

    public HibernateAuditEventListener(
            ObjectMapper objectMapper,
            ObjectProvider<AuditTransactionContext> auditTransactionContextProvider) {

        this.objectMapper = objectMapper;
        this.auditTransactionContextProvider =
                auditTransactionContextProvider;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        AuditMetadata metadata =
                AuditFunctionContext.get();

        if (metadata == null) {
            return false;
        }

        if (isAuditLog(event.getEntity())) {
            return false;
        }

        Map<String, Object> oldData =
                stateToMap(
                        event.getPersister(),
                        event.getDeletedState()
                );

        auditTransactionContextProvider.getObject().add(
                new AuditChange(
                        metadata.functionKey(),
                        AuditAction.DELETE.name(),
                        event.getPersister().getEntityName(),
                        String.valueOf(event.getId()),
                        oldData,
                        null
                )
        );

        return false;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        AuditMetadata metadata =
                AuditFunctionContext.get();

        if (metadata == null) {
            return false;
        }

        if (isAuditLog(event.getEntity())) {
            return false;
        }

        Map<String, Object> newData =
                stateToMap(
                        event.getPersister(),
                        event.getState()
                );

        auditTransactionContextProvider.getObject().add(
                new AuditChange(
                        metadata.functionKey(),
                        AuditAction.CREATE.name(),
                        event.getPersister().getEntityName(),
                        String.valueOf(event.getId()),
                        null,
                        newData
                )
        );

        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        AuditMetadata metadata =
                AuditFunctionContext.get();

        if (metadata == null) {
            return false;
        }

        if (isAuditLog(event.getEntity())) {
            return false;
        }

        Map<String, Object> oldData =
                stateToMap(
                        event.getPersister(),
                        event.getOldState()
                );

        Map<String, Object> newData =
                stateToMap(
                        event.getPersister(),
                        event.getState()
                );

        auditTransactionContextProvider.getObject().add(
                new AuditChange(
                        metadata.functionKey(),
                        AuditAction.UPDATE.name(),
                        event.getPersister().getEntityName(),
                        String.valueOf(event.getId()),
                        oldData,
                        newData
                )
        );

        return false;
    }

    private Map<String, Object> stateToMap(
            EntityPersister persister,
            Object[] state) {

        if (state == null) {
            return null;
        }

        String[] propertyNames =
                persister.getPropertyNames();

        Map<String, Object> result =
                new LinkedHashMap<>();

        for (int i = 0;
             i < propertyNames.length;
             i++) {

            Object value = state[i];

            result.put(
                    propertyNames[i],
                    normalize(value)
            );
        }

        return result;
    }

    private Object normalize(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum) {

            return value;
        }

        return String.valueOf(value);
    }

    private boolean isAuditLog(Object entity) {

        return entity instanceof AuditLog;
    }
}
