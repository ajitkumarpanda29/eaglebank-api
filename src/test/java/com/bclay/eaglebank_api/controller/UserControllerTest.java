package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.User;
import com.bclay.eaglebank_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        createdUser.setUsername(user.getUsername());

        when(userService.createUser(user)).thenReturn(createdUser);

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(createdUser, response.getBody());
        verify(userService).createUser(user);
    }

    @Test
    void createUser_missingFields_returnsBadRequest() {
        User user = new User(); // missing username, email, password

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Missing required fields", response.getBody());
        verify(userService, never()).createUser(any());
    }

    @Test
    void getUser_authorized_returnsUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Principal principal = () -> "testuser";

        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<?> response = userController.getUser(userId, principal);

        assertEquals(200, ((ResponseEntity<?>) response).getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUser_unauthorized_returnsForbidden() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("otheruser");

        Principal principal = () -> "testuser";

        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<?> response = userController.getUser(userId, principal);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("You are not authorized to access this user's data.", response.getBody());
    }

    @Test
    void updateUser_authorized_updatesAndReturnsUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("new@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.updateUser(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<User> response = userController.updateUser(userId, updatedUser, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("new@example.com", response.getBody().getEmail());
        verify(userService).updateUser(existingUser);
    }

    @Test
    void updateUser_unauthorized_returnsForbidden() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("otheruser");

        User updatedUser = new User();
        updatedUser.setUsername("testuser");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(userService.getUserById(userId)).thenReturn(existingUser);

        ResponseEntity<User> response = userController.updateUser(userId, updatedUser, auth);

        assertEquals(403, response.getStatusCodeValue());
        verify(userService, never()).updateUser(any());
    }

    @Test
    void deleteUser_authorized_noBankAccounts_deletesUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.userHasBankAccounts(userId)).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUser(userId, auth);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_authorized_withBankAccounts_returnsForbidden() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.userHasBankAccounts(userId)).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUser(userId, auth);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Cannot delete user with existing bank accounts", response.getBody());
        verify(userService, never()).deleteUser(any());
    }

    @Test
    void deleteUser_unauthorized_returnsForbidden() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("otheruser");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        when(userService.getUserById(userId)).thenReturn(existingUser);

        ResponseEntity<?> response = userController.deleteUser(userId, auth);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("You can only delete your own account", response.getBody());
        verify(userService, never()).deleteUser(any());
    }
}
