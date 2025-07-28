package com.bclay.eaglebank_api.controller;

import com.bclay.eaglebank_api.model.Transaction;
import com.bclay.eaglebank_api.model.TransactionType;
import com.bclay.eaglebank_api.service.BankAccountService;
import com.bclay.eaglebank_api.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(100.0);
        transaction.setType(TransactionType.DEPOSIT);

        when(transactionService.createTransaction(eq(accountId), any(Transaction.class)))
                .thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(accountId, transaction);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transaction.getId(), response.getBody().getId());
        verify(transactionService).createTransaction(accountId, transaction);
    }

    @Test
    void getAllTransactions_shouldReturnTransactionList() {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(100.0);
        transaction.setType(TransactionType.DEPOSIT);

        Transaction transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setAmount(100.0);
        transaction2.setType(TransactionType.DEPOSIT);

        List<Transaction> transactions = List.of(transaction, transaction2);

        when(transactionService.getAllTransactions(accountId)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions(accountId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(bankAccountService).getAccountById(accountId);  // Ensure account ownership is validated
        verify(transactionService).getAllTransactions(accountId);
    }

    @Test
    void getTransaction_shouldReturnTransaction() {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(200.0);
        transaction.setType(TransactionType.DEPOSIT);

        when(transactionService.getTransaction(accountId, transactionId)).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.getTransaction(accountId, transactionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactionId, response.getBody().getId());
        verify(transactionService).getTransaction(accountId, transactionId);
    }
}
