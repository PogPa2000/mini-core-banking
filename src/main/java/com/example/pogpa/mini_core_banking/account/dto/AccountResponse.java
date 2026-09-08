package com.example.pogpa.mini_core_banking.account.dto;

import com.example.pogpa.mini_core_banking.account.AccountStatus;
import com.example.pogpa.mini_core_banking.account.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class AccountResponse {
    private UUID accountId;

    private String accountNo;

    private UUID customerId;

    private Long accountTypeId;

    private Long branchId;

    private String currencyCode;

    private BigDecimal availableBalance;

    private BigDecimal ledgerBalance;

    private BigDecimal holdAmount;

    private AccountStatus status;

    private Instant openedAt;

    public AccountResponse(Account account) {

        this.accountId = account.getAccountId();
        this.accountNo = account.getAccountNo();
        this.customerId = account.getCustomerId();
        this.accountTypeId = account.getAccountTypeId();
        this.branchId = account.getBranchId();
        this.currencyCode = account.getCurrencyCode();
        this.availableBalance = account.getAvailableBalance();
        this.ledgerBalance = account.getLedgerBalance();
        this.holdAmount = account.getHoldAmount();
        this.status = account.getStatus();
        this.openedAt = account.getOpenedAt();
    }
}
