package com.example.pogpa.mini_core_banking.customer.controller;

import com.example.pogpa.mini_core_banking.customer.dto.CreateCustomerRequest;
import com.example.pogpa.mini_core_banking.customer.dto.CustomerResponse;
import com.example.pogpa.mini_core_banking.customer.dto.UpdateCustomerRequest;
import com.example.pogpa.mini_core_banking.customer.entity.Customer;
import com.example.pogpa.mini_core_banking.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> create(
            @Valid @RequestBody CreateCustomerRequest request) {

        Customer customer =
                customerService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }

    @PutMapping("/{customerNo}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable("customerNo") String customerNo,
            @Valid @RequestBody UpdateCustomerRequest request) {

        return ResponseEntity.ok(
                customerService.update(request, customerNo)
        );
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll(){
        return ResponseEntity.ok(customerService.getAll());
    }

    @GetMapping("/{customerNo}")
    public ResponseEntity<Optional<Customer>> getFindByCustomerNo(@PathVariable String customerNo){
        return ResponseEntity.ok(customerService.getByFindCustomerNo(customerNo));
    }

}
