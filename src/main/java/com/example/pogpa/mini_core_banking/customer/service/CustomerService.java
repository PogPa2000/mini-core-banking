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

        customer.setFullName(request.getFullName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setGender(request.getGender());

        return  new CustomerResponse(customer);
    }

    @Auditable(functionKey = "DELETE_CUSTOMER")
    @Transactional
    public void delete(String customerNo){
        Customer customer = customerRepository.findByCustomerNo(customerNo)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if(customer.getStatus() == CustomerStatus.BLOCKED){
            throw new RuntimeException("Customer deleted");
        }

        customer.setStatus(CustomerStatus.BLOCKED);
        customerRepository.save(customer);
    }

    private String generateCustomerNo() {
        return "cus_" + System.currentTimeMillis();
    }

    public List<CustomerResponse> getAll(){
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::new)
                .toList();
    }

    public CustomerResponse getByFindCustomerNo(String customerNo){
        Customer customer = customerRepository .findByCustomerNo(customerNo)
                .orElseThrow(() -> new RuntimeException( "Khach hang khong ton tai"));
        return new CustomerResponse(customer);
    }
}
