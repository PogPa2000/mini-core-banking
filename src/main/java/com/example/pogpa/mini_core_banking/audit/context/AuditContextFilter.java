package com.example.pogpa.mini_core_banking.audit.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditContextFilter extends OncePerRequestFilter {
    private final AuditContext auditContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String traceId = request.getHeader("X-Trace-Id");

            if (traceId == null || traceId.isEmpty()) {
                traceId = UUID.randomUUID().toString();
            }

            auditContext.setTraceId(traceId);

            // Tạm thời chưa có Spring Security
            auditContext.setActorId("system");

            auditContext.setIpAddress(
                    request.getRemoteAddr()
            );

            auditContext.setUserAgent(
                    request.getHeader("User-Agent")
            );

            filterChain.doFilter(request, response);

        } finally {
            auditContext.clear();
        }
    }
}
