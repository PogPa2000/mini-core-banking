-- ============================================================
-- MINI CORE BANKING - PostgreSQL
-- Flyway migration: V1 - create core banking schema
-- Database: mini_core_banking
-- PostgreSQL 18+
-- Run automatically by Spring Boot + Flyway
-- ============================================================

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS core;
SET search_path TO core, public;

-- ============================================================
-- 1. BRANCH
-- ============================================================
CREATE TABLE IF NOT EXISTS branch (
    branch_id        BIGSERIAL PRIMARY KEY,
    branch_code      VARCHAR(20) NOT NULL UNIQUE,
    branch_name      VARCHAR(200) NOT NULL,
    address          VARCHAR(500),
    phone            VARCHAR(30),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_branch_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- ============================================================
-- 2. CUSTOMER
-- ============================================================
CREATE TABLE IF NOT EXISTS customer (
    customer_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_no       VARCHAR(30) NOT NULL UNIQUE,
    customer_type     VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL',
    full_name         VARCHAR(200) NOT NULL,
    date_of_birth     DATE,
    gender            VARCHAR(20),
    national_id       VARCHAR(50) UNIQUE,
    phone             VARCHAR(30),
    email             VARCHAR(150),
    address           VARCHAR(500),
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    branch_id         BIGINT REFERENCES branch(branch_id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_customer_type CHECK (customer_type IN ('INDIVIDUAL', 'CORPORATE')),
    CONSTRAINT ck_customer_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    CONSTRAINT ck_customer_gender CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'))
);

-- ============================================================
-- 3. ACCOUNT TYPE
-- ============================================================
CREATE TABLE IF NOT EXISTS account_type (
    account_type_id   BIGSERIAL PRIMARY KEY,
    type_code         VARCHAR(30) NOT NULL UNIQUE,
    type_name         VARCHAR(100) NOT NULL,
    description       VARCHAR(500),
    currency_code     CHAR(3) NOT NULL DEFAULT 'VND',
    min_balance       NUMERIC(19,2) NOT NULL DEFAULT 0,
    interest_rate     NUMERIC(9,6) NOT NULL DEFAULT 0,
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_account_type_balance CHECK (min_balance >= 0),
    CONSTRAINT ck_account_type_interest CHECK (interest_rate >= 0),
    CONSTRAINT ck_account_type_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- ============================================================
-- 4. ACCOUNT
-- ============================================================
CREATE TABLE IF NOT EXISTS account (
    account_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_no          VARCHAR(30) NOT NULL UNIQUE,
    customer_id         UUID NOT NULL REFERENCES customer(customer_id),
    account_type_id     BIGINT NOT NULL REFERENCES account_type(account_type_id),
    branch_id           BIGINT REFERENCES branch(branch_id),
    currency_code       CHAR(3) NOT NULL DEFAULT 'VND',
    available_balance   NUMERIC(19,2) NOT NULL DEFAULT 0,
    ledger_balance      NUMERIC(19,2) NOT NULL DEFAULT 0,
    hold_amount         NUMERIC(19,2) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_account_available_balance CHECK (available_balance >= 0),
    CONSTRAINT ck_account_ledger_balance CHECK (ledger_balance >= 0),
    CONSTRAINT ck_account_hold_amount CHECK (hold_amount >= 0),
    CONSTRAINT ck_account_status CHECK (status IN ('ACTIVE', 'DORMANT', 'BLOCKED', 'CLOSED'))
);

-- ============================================================
-- 5. BENEFICIARY
-- ============================================================
CREATE TABLE IF NOT EXISTS beneficiary (
    beneficiary_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id         UUID NOT NULL REFERENCES customer(customer_id),
    beneficiary_name    VARCHAR(200) NOT NULL,
    bank_name           VARCHAR(200),
    account_no          VARCHAR(50) NOT NULL,
    bank_code           VARCHAR(30),
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_beneficiary_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED'))
);

-- ============================================================
-- 6. BANKING TRANSACTION
-- ============================================================
CREATE TABLE IF NOT EXISTS banking_transaction (
    transaction_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_no          VARCHAR(40) NOT NULL UNIQUE,
    transaction_type        VARCHAR(30) NOT NULL,
    transaction_status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    source_account_id       UUID REFERENCES account(account_id),
    destination_account_id  UUID REFERENCES account(account_id),
    amount                  NUMERIC(19,2) NOT NULL,
    fee_amount              NUMERIC(19,2) NOT NULL DEFAULT 0,
    tax_amount              NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency_code           CHAR(3) NOT NULL DEFAULT 'VND',
    description             VARCHAR(500),
    reference_no            VARCHAR(100),
    idempotency_key         VARCHAR(100) UNIQUE,
    requested_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_tx_amount CHECK (amount > 0),
    CONSTRAINT ck_tx_fee CHECK (fee_amount >= 0),
    CONSTRAINT ck_tx_tax CHECK (tax_amount >= 0),
    CONSTRAINT ck_tx_status CHECK (transaction_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'REVERSED')),
    CONSTRAINT ck_tx_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'PAYMENT', 'FEE', 'INTEREST', 'REVERSAL')),
    CONSTRAINT ck_tx_source_destination CHECK (source_account_id IS NOT NULL OR destination_account_id IS NOT NULL)
);

-- ============================================================
-- 7. LEDGER ENTRY - DOUBLE ENTRY ACCOUNTING
-- ============================================================
CREATE TABLE IF NOT EXISTS ledger_entry (
    ledger_entry_id       BIGSERIAL PRIMARY KEY,
    transaction_id        UUID NOT NULL REFERENCES banking_transaction(transaction_id),
    account_id            UUID NOT NULL REFERENCES account(account_id),
    entry_type            VARCHAR(10) NOT NULL,
    amount                NUMERIC(19,2) NOT NULL,
    currency_code         CHAR(3) NOT NULL DEFAULT 'VND',
    balance_before       NUMERIC(19,2) NOT NULL,
    balance_after        NUMERIC(19,2) NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_ledger_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT ck_ledger_amount CHECK (amount > 0)
);

-- ============================================================
-- 8. ACCOUNT HOLD
-- ============================================================
CREATE TABLE IF NOT EXISTS account_hold (
    hold_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id           UUID NOT NULL REFERENCES account(account_id),
    hold_reference       VARCHAR(100) NOT NULL UNIQUE,
    amount               NUMERIC(19,2) NOT NULL,
    reason               VARCHAR(500),
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at            TIMESTAMPTZ,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    released_at          TIMESTAMPTZ,
    CONSTRAINT ck_hold_amount CHECK (amount > 0),
    CONSTRAINT ck_hold_status CHECK (status IN ('ACTIVE', 'RELEASED', 'EXPIRED', 'CANCELLED'))
);

-- ============================================================
-- 9. DAILY ACCOUNT BALANCE
-- ============================================================
CREATE TABLE IF NOT EXISTS account_daily_balance (
    balance_id           BIGSERIAL PRIMARY KEY,
    account_id           UUID NOT NULL REFERENCES account(account_id),
    balance_date         DATE NOT NULL,
    opening_balance      NUMERIC(19,2) NOT NULL DEFAULT 0,
    closing_balance      NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_credit         NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_debit          NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_account_daily_balance UNIQUE (account_id, balance_date)
);

-- ============================================================
-- 10. AUDIT LOG
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id             BIGSERIAL PRIMARY KEY,
    trace_id             VARCHAR(100),
    actor_type           VARCHAR(30),
    actor_id             VARCHAR(100),
    action               VARCHAR(100) NOT NULL,
    entity_type          VARCHAR(100),
    entity_id            VARCHAR(100),
    old_data             JSONB,
    new_data             JSONB,
    ip_address           INET,
    user_agent           VARCHAR(500),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 11. API REQUEST / IDEMPOTENCY
-- ============================================================
CREATE TABLE IF NOT EXISTS api_request (
    request_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key      VARCHAR(100) NOT NULL UNIQUE,
    api_name             VARCHAR(200) NOT NULL,
    request_hash         VARCHAR(128),
    response_status      VARCHAR(20),
    response_body        JSONB,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at         TIMESTAMPTZ
);

-- ============================================================
-- 12. INDEXES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);
CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);
CREATE INDEX IF NOT EXISTS idx_customer_branch ON customer(branch_id);
CREATE INDEX IF NOT EXISTS idx_account_customer ON account(customer_id);
CREATE INDEX IF NOT EXISTS idx_account_status ON account(status);
CREATE INDEX IF NOT EXISTS idx_account_type ON account(account_type_id);
CREATE INDEX IF NOT EXISTS idx_transaction_source ON banking_transaction(source_account_id);
CREATE INDEX IF NOT EXISTS idx_transaction_destination ON banking_transaction(destination_account_id);
CREATE INDEX IF NOT EXISTS idx_transaction_status ON banking_transaction(transaction_status);
CREATE INDEX IF NOT EXISTS idx_transaction_created_at ON banking_transaction(created_at);
CREATE INDEX IF NOT EXISTS idx_ledger_transaction ON ledger_entry(transaction_id);
CREATE INDEX IF NOT EXISTS idx_ledger_account ON ledger_entry(account_id);
CREATE INDEX IF NOT EXISTS idx_ledger_created_at ON ledger_entry(created_at);
CREATE INDEX IF NOT EXISTS idx_hold_account ON account_hold(account_id);
CREATE INDEX IF NOT EXISTS idx_hold_status ON account_hold(status);
CREATE INDEX IF NOT EXISTS idx_audit_trace ON audit_log(trace_id);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_log(created_at);

-- ============================================================
-- 13. TRIGGER updated_at
-- ============================================================
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_branch_updated_at ON branch;
CREATE TRIGGER trg_branch_updated_at
BEFORE UPDATE ON branch
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

DROP TRIGGER IF EXISTS trg_customer_updated_at ON customer;
CREATE TRIGGER trg_customer_updated_at
BEFORE UPDATE ON customer
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

DROP TRIGGER IF EXISTS trg_account_updated_at ON account;
CREATE TRIGGER trg_account_updated_at
BEFORE UPDATE ON account
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

DROP TRIGGER IF EXISTS trg_banking_transaction_updated_at ON banking_transaction;
CREATE TRIGGER trg_banking_transaction_updated_at
BEFORE UPDATE ON banking_transaction
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

COMMIT;

-- ============================================================
-- 14. VERIFY
-- ============================================================
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema = 'core'
ORDER BY table_name;
