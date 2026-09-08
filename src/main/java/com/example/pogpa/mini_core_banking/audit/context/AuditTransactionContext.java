package com.example.pogpa.mini_core_banking.audit.context;

import com.example.pogpa.mini_core_banking.audit.metadata.AuditChange;
import com.example.pogpa.mini_core_banking.audit.service.AuditPersistenceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuditTransactionContext {
    private final AuditPersistenceService auditPersistenceService;

    private static final ThreadLocal<List<AuditChange>> CHANGES =
            new ThreadLocal<>();

    private static final ThreadLocal<Boolean> REGISTERED =
            new ThreadLocal<>();

    public AuditTransactionContext(
            AuditPersistenceService auditPersistenceService) {

        this.auditPersistenceService =
                auditPersistenceService;
    }

    public void add(AuditChange change) {

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            throw new IllegalStateException(
                    "Audit requires an active transaction"
            );
        }

        List<AuditChange> changes = CHANGES.get();

        if (changes == null) {

            changes = new ArrayList<>();

            CHANGES.set(changes);
        }

        changes.add(change);

        registerSynchronizationIfNecessary();
    }

    private void registerSynchronizationIfNecessary() {

        if (Boolean.TRUE.equals(REGISTERED.get())) {
            return;
        }

        REGISTERED.set(true);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        List<AuditChange> changes =
                                CHANGES.get();

                        if (changes == null ||
                                changes.isEmpty()) {
                            return;
                        }

                        auditPersistenceService.saveAll(
                                new ArrayList<>(changes)
                        );
                    }

                    @Override
                    public void afterCompletion(
                            int status) {

                        CHANGES.remove();
                        REGISTERED.remove();
                    }
                }
        );
    }
}
