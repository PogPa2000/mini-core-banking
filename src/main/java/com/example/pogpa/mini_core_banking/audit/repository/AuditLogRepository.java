package com.example.pogpa.mini_core_banking.audit.repository;

import com.example.pogpa.mini_core_banking.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
