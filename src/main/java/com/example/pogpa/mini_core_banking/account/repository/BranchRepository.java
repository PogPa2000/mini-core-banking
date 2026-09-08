package com.example.pogpa.mini_core_banking.account.repository;

import com.example.pogpa.mini_core_banking.account.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
}
