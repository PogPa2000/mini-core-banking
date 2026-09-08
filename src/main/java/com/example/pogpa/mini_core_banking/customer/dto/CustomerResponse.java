package com.example.pogpa.mini_core_banking.customer.dto;

import com.example.pogpa.mini_core_banking.customer.entity.Customer;
import com.example.pogpa.mini_core_banking.enums.GenderType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerResponse {
    private String fullName;
    private LocalDate dateOfBirth;
    private String nationalId;
    private String phone;
    private String email;
    private GenderType gender;

    public  CustomerResponse(Customer customer){
        this.fullName = customer.getFullName();
        this.dateOfBirth = customer.getDateOfBirth();
        this.phone = customer.getPhone();
        this.email = customer.getEmail();
        this.gender = customer.getGender();
    }

        // getter/setter
}
