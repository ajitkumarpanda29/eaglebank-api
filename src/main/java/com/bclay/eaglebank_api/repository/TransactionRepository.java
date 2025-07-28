package com.bclay.eaglebank_api.repository;

import com.bclay.eaglebank_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBankAccountId(UUID accountId);
}

