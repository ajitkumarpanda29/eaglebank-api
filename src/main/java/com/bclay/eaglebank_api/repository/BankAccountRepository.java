package com.bclay.eaglebank_api.repository;

import com.bclay.eaglebank_api.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}