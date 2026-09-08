package com.example.pogpa.mini_core_banking.account.entity;

import com.example.pogpa.mini_core_banking.account.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "account",
        schema = "core"
)
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private UUID accountId;

    @Column(
            name = "account_no",
            length = 30,
            nullable = false
    )
    private String accountNo;

    @Column(
            name = "customer_id",
            nullable = false
    )
    private UUID customerId;

    @Column(
            name = "account_type_id",
            nullable = false
    )
    private Long accountTypeId;

    @Column(name = "branch_id")
    private Long branchId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "currency_code",
            length = 3,
            nullable = false
    )
    private String currencyCode;

    @Column(
            name = "available_balance",
            precision = 19,
            scale = 2,
            nullable = false
    )
    private BigDecimal availableBalance;

    @Column(
            name = "ledger_balance",
            precision = 19,
            scale = 2,
            nullable = false
    )
    private BigDecimal ledgerBalance;

    @Column(
            name = "hold_amount",
            precision = 19,
            scale = 2,
            nullable = false
    )
    private BigDecimal holdAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            length = 20,
            nullable = false
    )
    private AccountStatus status;

    @Column(
            name = "opened_at",
            nullable = false
    )
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;

    @PrePersist
    protected void onCreate() {

        Instant now = Instant.now();

        if (currencyCode == null) {
            currencyCode = "VND";
        }

        if (availableBalance == null) {
            availableBalance = BigDecimal.ZERO;
        }

        if (ledgerBalance == null) {
            ledgerBalance = BigDecimal.ZERO;
        }

        if (holdAmount == null) {
            holdAmount = BigDecimal.ZERO;
        }

        if (status == null) {
            status = AccountStatus.BLOCKED;
        }

        if (openedAt == null) {
            openedAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
