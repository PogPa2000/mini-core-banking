package com.example.pogpa.mini_core_banking.audit.context;

import org.springframework.stereotype.Component;

@Component
public class AuditContext {
    private static final ThreadLocal<String> TRACE_ID =
            new ThreadLocal<>();

    private static final ThreadLocal<String> ACTOR_ID =
            new ThreadLocal<>();

    private static final ThreadLocal<String> IP_ADDRESS =
            new ThreadLocal<>();

    private static final ThreadLocal<String> USER_AGENT =
            new ThreadLocal<>();

    public void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public String getTraceId() {
        return TRACE_ID.get();
    }

    public void setActorId(String actorId) {
        ACTOR_ID.set(actorId);
    }

    public String getActorId() {
        return ACTOR_ID.get();
    }

    public void setIpAddress(String ipAddress) {
        IP_ADDRESS.set(ipAddress);
    }

    public String getIpAddress() {
        return IP_ADDRESS.get();
    }

    public void setUserAgent(String userAgent) {
        USER_AGENT.set(userAgent);
    }

    public String getUserAgent() {
        return USER_AGENT.get();
    }

    public void clear() {
        TRACE_ID.remove();
        ACTOR_ID.remove();
        IP_ADDRESS.remove();
        USER_AGENT.remove();
    }
}
