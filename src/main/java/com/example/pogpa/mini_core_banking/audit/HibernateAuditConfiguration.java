package com.example.pogpa.mini_core_banking.audit;

import com.example.pogpa.mini_core_banking.audit.context.AuditTransactionContext;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Configuration
public class HibernateAuditConfiguration
        implements HibernatePropertiesCustomizer {

    private final ObjectProvider<AuditTransactionContext>
            auditTransactionContextProvider;

    private final ObjectMapper objectMapper;

    public HibernateAuditConfiguration(
            ObjectProvider<AuditTransactionContext>
                    auditTransactionContextProvider,
            ObjectMapper objectMapper) {

        this.auditTransactionContextProvider =
                auditTransactionContextProvider;

        this.objectMapper = objectMapper;
    }

    @Override
    public void customize(Map<String, Object> properties) {

        properties.put(
                "hibernate.integrator_provider",
                (IntegratorProvider) () ->
                        List.of(
                                new AuditIntegrator(
                                        auditTransactionContextProvider,
                                        objectMapper
                                )
                        )
        );
    }

    private static class AuditIntegrator
            implements Integrator {

        private final ObjectProvider<AuditTransactionContext>
                auditTransactionContextProvider;

        private final ObjectMapper objectMapper;

        private AuditIntegrator(
                ObjectProvider<AuditTransactionContext>
                        auditTransactionContextProvider,
                ObjectMapper objectMapper) {

            this.auditTransactionContextProvider =
                    auditTransactionContextProvider;

            this.objectMapper = objectMapper;
        }

        @Override
        public void integrate(
                Metadata metadata,
                BootstrapContext bootstrapContext,
                SessionFactoryImplementor sessionFactory) {

            EventListenerRegistry registry =
                    sessionFactory
                            .getServiceRegistry()
                            .getService(
                                    EventListenerRegistry.class
                            );

            HibernateAuditEventListener listener =
                    new HibernateAuditEventListener(
                            objectMapper,
                            auditTransactionContextProvider
                    );

            registry.appendListeners(
                    EventType.PRE_INSERT,
                    listener
            );

            registry.appendListeners(
                    EventType.PRE_UPDATE,
                    listener
            );

            registry.appendListeners(
                    EventType.PRE_DELETE,
                    listener
            );
        }

        @Override
        public void disintegrate(
                SessionFactoryImplementor sessionFactory,
                SessionFactoryServiceRegistry serviceRegistry) {
        }
    }
}