package com.example.pogpa.mini_core_banking.customer.entity;

import com.example.pogpa.mini_core_banking.enums.CustomerStatus;
import com.example.pogpa.mini_core_banking.enums.GenderType;
import com.example.pogpa.mini_core_banking.enums.GenderTypeConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "customer", schema = "core",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_national_id",
                        columnNames = "national_id"),
                @UniqueConstraint(name = "uk_customer_phone",
                        columnNames = "phone")
        }
)
@Data
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue
    private UUID customerId;

    @Column(nullable = false, length = 200)
    private String customerNo;

    @Column(nullable = false, length = 200)
    private String customerType;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 50)
    private String nationalId;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "gender")
    @Convert(converter = GenderTypeConverter.class)
    private GenderType gender;

    @Column(name = "branch_id", nullable = false)
    private Integer  branchId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = CustomerStatus.ACTIVE;
        }

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

