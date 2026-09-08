package com.example.pogpa.mini_core_banking.audit.aspect;

import com.example.pogpa.mini_core_banking.audit.annotation.Auditable;

import com.example.pogpa.mini_core_banking.audit.context.AuditFunctionContext;
import com.example.pogpa.mini_core_banking.audit.metadata.AuditMetadata;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class AuditAspect {
    @Around("@annotation(auditable)")
    public Object audit(
            ProceedingJoinPoint joinPoint,
            Auditable auditable) throws Throwable {

        AuditMetadata previous =
                AuditFunctionContext.get();

        try {

            AuditFunctionContext.set(
                    new AuditMetadata(
                            auditable.functionKey()
                    )
            );

            return joinPoint.proceed();

        } finally {

            if (previous == null) {
                AuditFunctionContext.clear();
            } else {
                AuditFunctionContext.set(previous);
            }
        }
    }
}



