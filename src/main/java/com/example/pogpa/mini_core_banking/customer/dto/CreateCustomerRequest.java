package com.example.pogpa.mini_core_banking.customer.dto;

import com.example.pogpa.mini_core_banking.enums.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
@Data
public class CreateCustomerRequest {
    private String customerType;
    private String fullName;
    private LocalDate dateOfBirth;
    private String nationalId;
    private String phone;
    private String email;
    private GenderType gender;
    private int branchId;

        // getter/setter
}
