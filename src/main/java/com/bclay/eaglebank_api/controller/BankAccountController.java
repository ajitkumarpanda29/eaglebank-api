package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.BankAccount;
import com.bclay.eaglebank_api.service.BankAccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
public class BankAccountController {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountController.class);

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@Valid @RequestBody BankAccount account) {
        logger.info("Request to create a new bank account");
        BankAccount created = bankAccountService.createAccount(account);
        logger.info("Bank account created with ID: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAccountsForCurrentUser() {
        logger.info("Fetching all bank accounts for the authenticated user");
        List<BankAccount> accounts = bankAccountService.getAccountsForCurrentUser();
        logger.info("Found {} accounts for current user", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccount> getAccount(@PathVariable UUID accountId) {
        logger.info("Fetching bank account with ID: {}", accountId);
        BankAccount account = bankAccountService.getAccountById(accountId);
        logger.info("Retrieved bank account ID: {}, balance: {}", account.getId(), account.getBalance());
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<BankAccount> updateAccount(@PathVariable UUID accountId,
                                                     @RequestBody BankAccount updatedAccount) {
        logger.info("Request to update bank account with ID: {}", accountId);
        BankAccount savedAccount = bankAccountService.updateAccount(accountId, updatedAccount);
        logger.info("Bank account with ID: {} updated successfully", savedAccount.getId());
        return ResponseEntity.ok(savedAccount);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID accountId) {
        logger.info("Request to delete bank account with ID: {}", accountId);
        bankAccountService.deleteAccount(accountId);
        logger.info("Bank account with ID: {} deleted", accountId);
        return ResponseEntity.noContent().build();
    }
}
