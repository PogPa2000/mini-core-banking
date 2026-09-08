package com.example.pogpa.mini_core_banking.account.service;

import com.example.pogpa.mini_core_banking.account.dto.AccountResponse;
import com.example.pogpa.mini_core_banking.account.dto.OpenAccountRequest;
import com.example.pogpa.mini_core_banking.account.dto.UpdateAccountRequest;
import com.example.pogpa.mini_core_banking.account.entity.Account;
import com.example.pogpa.mini_core_banking.account.enums.AccountStatus;
import com.example.pogpa.mini_core_banking.account.repository.AccountRepository;
import com.example.pogpa.mini_core_banking.account.repository.BranchRepository;
import com.example.pogpa.mini_core_banking.audit.annotation.Auditable;
import com.example.pogpa.mini_core_banking.customer.entity.Customer;
import com.example.pogpa.mini_core_banking.customer.repository.CustomerRepository;
import com.example.pogpa.mini_core_banking.enums.CustomerStatus;
import com.example.pogpa.mini_core_banking.exception.DataNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          BranchRepository branchRepository){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.branchRepository = branchRepository;
    }

    public List<AccountResponse> getAccounts(){
        return accountRepository.findAll()
                .stream()
                .map(AccountResponse::new)
                .toList();
    }

    public AccountResponse getAccountByAccountNo(String accountNo){
        Account account = accountRepository.findByAccountNo(accountNo).orElseThrow(() -> new RuntimeException("Account not found"));
        return new AccountResponse(account);
    }

    @Auditable(functionKey = "OPEN_ACCOUNT")
    @Transactional
    public AccountResponse openAccount(OpenAccountRequest openAccountRequest){
        UUID customerId = openAccountRequest.customerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotFoundException("Data Not Found"));

        if (customer.getStatus() != CustomerStatus.ACTIVE){
            throw new DataNotFoundException("Khach hang da bi khoa khong the mo tai khoang");
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

    @Auditable(functionKey = "ACCOUNT_UPDATE")
    @Transactional
    public AccountResponse updateAccount(String accountNo, UpdateAccountRequest updateAccountRequest){
        //Kiem tra so tai khoang da co trong he thong chua
       Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new DataNotFoundException("Account not found in System"));

        //tai khoan phai o trang thai active
        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("tai khoang khong o trang thai active");
        }

        //Chi cap nhat chi nhanh ngan hang
        //kiem tra chi nhanh ngan hang co ton tai trong he thong khong
        Long branchId = updateAccountRequest.branchId();
        branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Ma chi nhanh khong ton tai trong he thong"));

        account.setBranchId(updateAccountRequest.branchId());
        account.setVersion(updateAccountRequest.version());
        accountRepository.save(account);

        return new AccountResponse(account);
    }



    private String generateAccountNo(){
        return "ACC_" + System.currentTimeMillis();
    }
}
