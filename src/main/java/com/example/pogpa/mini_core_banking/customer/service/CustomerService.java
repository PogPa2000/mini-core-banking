package com.example.pogpa.mini_core_banking.customer.service;

import com.example.pogpa.mini_core_banking.audit.annotation.Auditable;
import com.example.pogpa.mini_core_banking.customer.dto.CreateCustomerRequest;
import com.example.pogpa.mini_core_banking.customer.dto.CustomerResponse;
import com.example.pogpa.mini_core_banking.customer.dto.UpdateCustomerRequest;
import com.example.pogpa.mini_core_banking.customer.entity.Customer;
import com.example.pogpa.mini_core_banking.customer.repository.CustomerRepository;
import com.example.pogpa.mini_core_banking.enums.CustomerStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Auditable(functionKey = "CUSTOMER_CREATE")
    @Transactional
    public Customer create(CreateCustomerRequest request){

        Customer customer = new Customer();
        customer.setCustomerNo(generateCustomerNo());
        customer.setFullName(request.getFullName());
        customer.setCustomerType(request.getCustomerType());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setGender(request.getGender());

        return customerRepository.save(customer);
    }

    @Auditable(functionKey = "CUSTOMER_UPDATE")
    @Transactional

    public CustomerResponse update(
            UpdateCustomerRequest request,
            String customerNo) {

        Customer customer = customerRepository
                .findByCustomerNo(customerNo)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Khach hang khong ton tai"));

        Customer oldCustomer = new Customer();
        oldCustomer.setFullName(customer.getFullName());
        oldCustomer.setDateOfBirth(customer.getDateOfBirth());
        oldCustomer.setPhone(customer.getPhone());
        oldCustomer.setEmail(customer.getEmail());
        oldCustomer.setGender(customer.getGender());

        customer.setFullName(request.getFullName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setGender(request.getGender());

        CustomerResponse response =
                new CustomerResponse(customer);

        return response;
    }

    private String generateCustomerNo() {
        return "cus_" + System.currentTimeMillis();
    }

    public List<Customer> getAll(){
        return customerRepository.findAll();
    }

    public Optional<Customer> getByFindCustomerNo(String customerNo){
        return customerRepository.findByCustomerNo(customerNo);
    }
}
