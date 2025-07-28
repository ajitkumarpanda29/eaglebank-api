package com.bclay.eaglebank_api.service;

import com.bclay.eaglebank_api.exception.UserNotFoundException;
import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.repository.BankAccountRepository;
import com.bclay.eaglebank_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BankAccountRepository bankAccountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        logger.info("Creating user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User getUserById(UUID userId) {
        logger.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
    }

    public User updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());
        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        logger.info("Attempting to delete user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            logger.warn("Cannot delete. User with ID {} does not exist", userId);
            throw new UserNotFoundException("Cannot delete. User with ID " + userId + " does not exist.");
        }
        userRepository.deleteById(userId);
        logger.info("User with ID {} deleted successfully", userId);
    }

    public boolean userHasBankAccounts(UUID userId) {
        logger.debug("Checking if user with ID {} has bank accounts", userId);
        boolean hasAccounts = bankAccountRepository.existsByUserId(userId);
        logger.debug("User with ID {} has accounts: {}", userId, hasAccounts);
        return hasAccounts;
    }
}
