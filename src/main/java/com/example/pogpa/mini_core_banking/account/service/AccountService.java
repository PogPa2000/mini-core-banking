package com.example.pogpa.mini_core_banking.account.service;

import com.example.pogpa.mini_core_banking.account.AccountStatus;
import com.example.pogpa.mini_core_banking.account.dto.AccountResponse;
import com.example.pogpa.mini_core_banking.account.dto.OpenAccountRequest;
import com.example.pogpa.mini_core_banking.account.entity.Account;
import com.example.pogpa.mini_core_banking.account.repository.AccountRepository;
import com.example.pogpa.mini_core_banking.audit.annotation.Auditable;
import com.example.pogpa.mini_core_banking.customer.entity.Customer;
import com.example.pogpa.mini_core_banking.customer.repository.CustomerRepository;
import com.example.pogpa.mini_core_banking.enums.CustomerStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public List<AccountResponse> getAccounts(){
        return accountRepository.findAll()
                .stream()
                .map(AccountResponse::new)
                .toList();
    }

    @Auditable(functionKey = "OPEN_ACCOUNT")
    @Transactional
    public AccountResponse openAccount(OpenAccountRequest openAccountRequest){
        UUID customerId = openAccountRequest.customerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Data Not Found"));

        if (customer.getStatus() != CustomerStatus.ACTIVE){
            throw new RuntimeException("Khach hang da bi khoa khong the mo tai khoang");
        }

        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountTypeId(openAccountRequest.accountTypeId());
        account.setBranchId(openAccountRequest.branchId());
        account.setCurrencyCode(openAccountRequest.currencyCode());
        account.setAccountNo(this.generateAccountNo());

        accountRepository.save(account);
        return  new AccountResponse(account);
    }

    private String generateAccountNo(){
        return "ACC_" + System.currentTimeMillis();
    }
}
