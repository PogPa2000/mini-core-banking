package com.example.pogpa.mini_core_banking.audit.service;

import com.example.pogpa.mini_core_banking.audit.metadata.AuditChange;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
public class AuditPersistenceService {
    private final AuditService auditService;

    public AuditPersistenceService(
            AuditService auditService) {

        this.auditService = auditService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(List<AuditChange> changes) {

        for (AuditChange change : changes) {

            auditService.save(
                    change.functionKey(),
                    change.entityType(),
                    change.entityId(),
                    change.action(),
                    change.oldData(),
                    change.newData()
            );
        }
    }
}
