package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Attempting to create user: {}", user.getUsername());
        if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null) {
            logger.warn("Missing required fields for user creation: {}", user);
            return ResponseEntity.badRequest().body("Missing required fields");
        }
        User created = userService.createUser(user);
        logger.info("User created with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId, Principal principal) {
        String currentUsername = principal.getName();
        logger.info("User '{}' requested user data for userId: {}", currentUsername, userId);

        User requestedUser = userService.getUserById(userId);

        if (!requestedUser.getUsername().equals(currentUsername)) {
            logger.warn("Unauthorized access attempt by '{}' to userId: {}", currentUsername, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to access this user's data.");
        }

        logger.info("User '{}' successfully retrieved their data", currentUsername);
        return ResponseEntity.ok(requestedUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId,
                                           @RequestBody User updatedUser,
                                           Authentication authentication) {
        String currentUsername = authentication.getName();
        logger.info("User '{}' attempting to update userId: {}", currentUsername, userId);

        User existingUser = userService.getUserById(userId);

        if (!existingUser.getUsername().equals(currentUsername)) {
            logger.warn("Unauthorized update attempt by '{}' on userId: {}", currentUsername, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (updatedUser.getUsername() != null) {
            logger.debug("Updating username to '{}'", updatedUser.getUsername());
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null) {
            logger.debug("Updating email to '{}'", updatedUser.getEmail());
            existingUser.setEmail(updatedUser.getEmail());
        }

        User savedUser = userService.updateUser(existingUser);
        logger.info("User '{}' successfully updated userId: {}", currentUsername, userId);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId, Authentication authentication) {
        String currentUsername = authentication.getName();
        logger.info("User '{}' attempting to delete userId: {}", currentUsername, userId);

        User existingUser = userService.getUserById(userId);

        if (!existingUser.getUsername().equals(currentUsername)) {
            logger.warn("Unauthorized delete attempt by '{}' on userId: {}", currentUsername, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only delete your own account");
        }

        if (userService.userHasBankAccounts(userId)) {
            logger.warn("User '{}' has bank accounts. Cannot delete.", currentUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cannot delete user with existing bank accounts");
        }

        userService.deleteUser(userId);
        logger.info("User '{}' successfully deleted their account (userId: {})", currentUsername, userId);
        return ResponseEntity.noContent().build();
    }
}
