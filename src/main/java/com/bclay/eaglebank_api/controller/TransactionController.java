package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.Transaction;
import com.bclay.eaglebank_api.service.BankAccountService;
import com.bclay.eaglebank_api.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts/{accountId}/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;

    public TransactionController(TransactionService transactionService, BankAccountService bankAccountService) {
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@PathVariable UUID accountId,
                                                         @Valid @RequestBody Transaction transaction) {
        logger.info("Request to create transaction for account ID: {}", accountId);
        Transaction created = transactionService.createTransaction(accountId, transaction);
        logger.info("Transaction created with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable UUID accountId) {
        logger.info("Fetching all transactions for account ID: {}", accountId);
        bankAccountService.getAccountById(accountId); // performs ownership check
        List<Transaction> transactions = transactionService.getAllTransactions(accountId);
        logger.info("Found {} transactions for account ID: {}", transactions.size(), accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable UUID accountId,
                                                      @PathVariable UUID transactionId) {
        logger.info("Fetching transaction ID: {} for account ID: {}", transactionId, accountId);
        Transaction tx = transactionService.getTransaction(accountId, transactionId);
        logger.info("Transaction retrieved: ID={}, Amount={}, Type={}", tx.getId(), tx.getAmount(), tx.getType());
        return ResponseEntity.ok(tx);
    }
}
