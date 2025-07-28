package com.bclay.eaglebank_api.service;

import com.bclay.eaglebank_api.exception.BankAccountNotFoundException;
import com.bclay.eaglebank_api.exception.UserNotFoundException;
import com.bclay.eaglebank_api.model.BankAccount;
import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.repository.BankAccountRepository;
import com.bclay.eaglebank_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public BankAccount createAccount(BankAccount account) {
        logger.info("Creating bank account for user ID: {}", account.getUser().getId());
        return bankAccountRepository.save(account);
    }

    public BankAccount getAccountById(UUID accountId) {
        logger.debug("Fetching bank account by ID: {}", accountId);
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Bank account not found: {}", accountId);
                    return new BankAccountNotFoundException("Bank account not found");
                });

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Authenticated user: {}", currentUsername);

        UUID accountOwnerId = account.getUser().getId();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found in repository: {}", currentUsername);
                    return new UserNotFoundException("Authenticated user not found");
                });

        if (!accountOwnerId.equals(currentUser.getId())) {
            logger.warn("Access denied for user {} to bank account {}", currentUsername, accountId);
            throw new AccessDeniedException("You are not authorized to access this bank account.");
        }

        logger.debug("User {} authorized to access bank account {}", currentUsername, accountId);
        return account;
    }

    public List<BankAccount> getAccountsByUserId(UUID userId) {
        logger.debug("Fetching all bank accounts for user ID: {}", userId);
        return bankAccountRepository.findByUserId(userId);
    }

    public void deleteAccount(UUID accountId) {
        logger.info("Deleting bank account with ID: {}", accountId);

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Bank account not found for deletion: {}", accountId);
                    return new BankAccountNotFoundException("Bank account not found");
                });

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found in repository: {}", currentUsername);
                    return new UserNotFoundException("Authenticated user not found");
                });

        if (!account.getUser().getId().equals(currentUser.getId())) {
            logger.warn("Access denied: user {} attempted to delete bank account {}", currentUsername, accountId);
            throw new AccessDeniedException("You are not authorized to delete this bank account.");
        }

        bankAccountRepository.delete(account);
        logger.info("Bank account {} deleted by user {}", accountId, currentUsername);
    }

    public BankAccount updateAccount(UUID accountId, BankAccount updatedAccount) {
        logger.info("Updating bank account with ID: {}", accountId);
        BankAccount existingAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.warn("Bank account not found for update: {}", accountId);
                    return new BankAccountNotFoundException("Bank account not found");
                });

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!existingAccount.getUser().getUsername().equals(currentUsername)) {
            logger.warn("Access denied: user {} attempted to update bank account {}", currentUsername, accountId);
            throw new AccessDeniedException("You are not authorized to update this bank account.");
        }

        if (updatedAccount.getAccountNumber() != null) {
            existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
            logger.debug("Updated account number to {}", updatedAccount.getAccountNumber());
        }
        if (updatedAccount.getBalance() != null) {
            existingAccount.setBalance(updatedAccount.getBalance());
            logger.debug("Updated balance to {}", updatedAccount.getBalance());
        }

        return bankAccountRepository.save(existingAccount);
    }

    public List<BankAccount> getAccountsForCurrentUser() {
        UUID currentUserId = getCurrentUserId();
        logger.debug("Fetching bank accounts for current user ID: {}", currentUserId);
        return bankAccountRepository.findByUserId(currentUserId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // assuming username is UUID string
        logger.debug("Current authenticated user ID string: {}", userIdString);
        return UUID.fromString(userIdString);
    }
}
