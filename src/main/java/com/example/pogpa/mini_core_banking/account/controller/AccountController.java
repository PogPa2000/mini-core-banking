package com.example.pogpa.mini_core_banking.account.controller;

import com.example.pogpa.mini_core_banking.account.dto.AccountResponse;
import com.example.pogpa.mini_core_banking.account.dto.OpenAccountRequest;
import com.example.pogpa.mini_core_banking.account.dto.UpdateAccountRequest;
import com.example.pogpa.mini_core_banking.account.entity.Account;
import com.example.pogpa.mini_core_banking.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(){
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/{accountNo}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNo){
        return ResponseEntity.ok(accountService.getAccountByAccountNo(accountNo));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(
            @Valid @RequestBody OpenAccountRequest openAccountRequest
            ){
        return ResponseEntity.ok(accountService.openAccount(openAccountRequest));
    }

    @PutMapping("/{accountNo}")
    public ResponseEntity<AccountResponse> updateBranchIdForAccount(
            @Valid @RequestBody UpdateAccountRequest updateAccountRequest,
            @PathVariable String accountNo
    ){
        return ResponseEntity.ok(accountService.updateAccount(accountNo, updateAccountRequest));
    }
}
