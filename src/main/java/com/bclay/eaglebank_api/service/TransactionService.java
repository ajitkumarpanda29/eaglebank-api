package com.bclay.eaglebank_api.service;

import com.bclay.eaglebank_api.exception.TransactionNotFoundException;
import com.bclay.eaglebank_api.exception.UserNotFoundException;
import com.bclay.eaglebank_api.model.BankAccount;
import com.bclay.eaglebank_api.model.Transaction;
import com.bclay.eaglebank_api.model.TransactionType;
import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.repository.TransactionRepository;
import com.bclay.eaglebank_api.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankAccountService bankAccountService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankAccountService = bankAccountService;
    }

    public Transaction createTransaction(UUID accountId, Transaction transaction) {
        logger.debug("Creating transaction for accountId={}", accountId);

        BankAccount account = bankAccountService.getAccountById(accountId);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    logger.warn("Authenticated user '{}' not found", currentUsername);
                    return new UserNotFoundException("Authenticated user not found");
                });

        if (!account.getUser().getId().equals(currentUser.getId())) {
            logger.warn("User '{}' is not authorized to perform transaction on account {}", currentUsername, accountId);
            throw new AccessDeniedException("You are not authorized to perform this transaction.");
        }

        if (transaction.getType() == null) {
            logger.error("Transaction type is missing");
            throw new IllegalArgumentException("Transaction type is required");
        }

        if (transaction.getType() == TransactionType.WITHDRAWAL &&
                account.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance for withdrawal: accountId={}, balance={}, requested={}",
                    accountId, account.getBalance(), transaction.getAmount());
            throw new IllegalArgumentException("Insufficient balance");
        }

        double updatedBalance = transaction.getType() == TransactionType.DEPOSIT
                ? account.getBalance() + transaction.getAmount()
                : account.getBalance() - transaction.getAmount();

        account.setBalance(updatedBalance);

        transaction.setTimestamp(LocalDateTime.now());
        transaction.setBankAccount(account);

        bankAccountService.updateAccount(accountId, account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Transaction {} created successfully for account {}", savedTransaction.getId(), accountId);
        return savedTransaction;
    }

    public List<Transaction> getAllTransactions(UUID accountId) {
        logger.debug("Fetching all transactions for accountId={}", accountId);
        bankAccountService.getAccountById(accountId); // enforces ownership
        return transactionRepository.findByBankAccountId(accountId);
    }

    public Transaction getTransaction(UUID accountId, UUID transactionId) {
        logger.debug("Fetching transaction {} for account {}", transactionId, accountId);

        BankAccount account = bankAccountService.getAccountById(accountId); // verifies ownership

        return transactionRepository.findById(transactionId)
                .filter(tx -> tx.getBankAccount().getId().equals(account.getId()))
                .orElseThrow(() -> {
                    logger.warn("Transaction {} not found or does not belong to account {}", transactionId, accountId);
                    return new TransactionNotFoundException("Transaction not found for this account");
                });
    }

    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        logger.debug("Fetching transactions (alt path) for accountId={}", accountId);
        return transactionRepository.findByBankAccountId(accountId);
    }
}

