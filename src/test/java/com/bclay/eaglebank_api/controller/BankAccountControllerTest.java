package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.BankAccount;
import com.bclay.eaglebank_api.model.TransactionType;
import com.bclay.eaglebank_api.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController bankAccountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_shouldReturnCreatedAccount() {
        BankAccount request = new BankAccount();
        request.setAccountNumber("123456");
        request.setBalance(1000.0);

        BankAccount saved = new BankAccount();
        saved.setId(UUID.randomUUID());
        saved.setAccountNumber(request.getAccountNumber());
        saved.setBalance(request.getBalance());

        when(bankAccountService.createAccount(request)).thenReturn(saved);

        ResponseEntity<BankAccount> response = bankAccountController.createAccount(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
        verify(bankAccountService).createAccount(request);
    }

    @Test
    void getAccountsForCurrentUser_shouldReturnListOfAccounts() {

        List<BankAccount> accounts = List.of(
                new BankAccount(UUID.randomUUID(), TransactionType.DEPOSIT.toString(), "1111", 500.0, null),
                new BankAccount(UUID.randomUUID(), TransactionType.DEPOSIT.toString(),"2222", 1200.0, null)
        );

        when(bankAccountService.getAccountsForCurrentUser()).thenReturn(accounts);

        ResponseEntity<List<BankAccount>> response = bankAccountController.getAccountsForCurrentUser();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(accounts, response.getBody());
        verify(bankAccountService).getAccountsForCurrentUser();
    }

    @Test
    void getAccount_shouldReturnAccount() {
        UUID accountId = UUID.randomUUID();
        BankAccount account = new BankAccount();
        account.setId(accountId);
        account.setAccountNumber("9999");
        account.setBalance(2500.0);

        when(bankAccountService.getAccountById(accountId)).thenReturn(account);

        ResponseEntity<BankAccount> response = bankAccountController.getAccount(accountId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(account, response.getBody());
        verify(bankAccountService).getAccountById(accountId);
    }

    @Test
    void updateAccount_shouldReturnUpdatedAccount() {
        UUID accountId = UUID.randomUUID();
        BankAccount updateRequest = new BankAccount();
        updateRequest.setAccountNumber("updated");
        updateRequest.setBalance(9000.0);

        BankAccount updated = new BankAccount();
        updated.setId(accountId);
        updated.setAccountNumber("updated");
        updated.setBalance(9000.0);

        when(bankAccountService.updateAccount(accountId, updateRequest)).thenReturn(updated);

        ResponseEntity<BankAccount> response = bankAccountController.updateAccount(accountId, updateRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        verify(bankAccountService).updateAccount(accountId, updateRequest);
    }

    @Test
    void deleteAccount_shouldReturnNoContent() {
        UUID accountId = UUID.randomUUID();

        doNothing().when(bankAccountService).deleteAccount(accountId);

        ResponseEntity<Void> response = bankAccountController.deleteAccount(accountId);

        assertEquals(204, response.getStatusCodeValue());
        verify(bankAccountService).deleteAccount(accountId);
    }
}
