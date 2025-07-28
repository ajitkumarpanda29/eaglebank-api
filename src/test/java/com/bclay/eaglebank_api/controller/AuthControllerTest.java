package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.AuthRequest;
import com.bclay.eaglebank_api.model.AuthResponse;
import com.bclay.eaglebank_api.service.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ReturnsAuthResponseWithToken() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        AuthRequest request = new AuthRequest(username, password);

        Authentication authMock = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(jwtUtil.generateToken(username)).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(username);
    }
}
