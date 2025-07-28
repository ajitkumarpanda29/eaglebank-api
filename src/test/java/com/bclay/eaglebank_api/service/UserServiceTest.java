package com.bclay.eaglebank_api.service;

import com.bclay.eaglebank_api.exception.UserNotFoundException;
import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.repository.BankAccountRepository;
import com.bclay.eaglebank_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainpassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedpassword");

        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(user);

        assertEquals(savedUser.getId(), result.getId());
        assertEquals("encodedpassword", result.getPassword());
        verify(passwordEncoder).encode("plainpassword");
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("existinguser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        assertEquals("existinguser", result.getUsername());
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_shouldSaveAndReturnUpdatedUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("updateduser");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals(user.getId(), result.getId());
        assertEquals("updateduser", result.getUsername());
    }

    @Test
    void deleteUser_shouldDeleteWhenExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void userHasBankAccounts_shouldReturnTrueIfExists() {
        UUID userId = UUID.randomUUID();
        when(bankAccountRepository.existsByUserId(userId)).thenReturn(true);

        assertTrue(userService.userHasBankAccounts(userId));
    }

    @Test
    void userHasBankAccounts_shouldReturnFalseIfNotExists() {
        UUID userId = UUID.randomUUID();
        when(bankAccountRepository.existsByUserId(userId)).thenReturn(false);

        assertFalse(userService.userHasBankAccounts(userId));
    }
}
