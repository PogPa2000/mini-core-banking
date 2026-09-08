package com.example.pogpa.mini_core_banking.account.controller;

import com.example.pogpa.mini_core_banking.account.dto.AccountResponse;
import com.example.pogpa.mini_core_banking.account.dto.OpenAccountRequest;
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

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(
            @Valid @RequestBody OpenAccountRequest openAccountRequest
            ){
        return ResponseEntity.ok(accountService.openAccount(openAccountRequest));
    }
}
