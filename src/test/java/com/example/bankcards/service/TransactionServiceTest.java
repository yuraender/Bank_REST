package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.transaction.DepositRequest;
import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.entity.CardNotFoundException;
import com.example.bankcards.exception.entity.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void deposit_ValidRequest_CreatesTransaction() {
        Card card = createTestCard();
        DepositRequest request = new DepositRequest(1L, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");

        TransactionDto result = transactionService.deposit(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500), card.getBalance());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_NonExistingCard_ThrowsException() {
        DepositRequest request = new DepositRequest(1L, BigDecimal.valueOf(500));
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> transactionService.deposit(request));
        verify(cardRepository).findById(1L);
        verifyNoMoreInteractions(cardRepository, transactionRepository);
    }

    @Test
    void transfer_ValidRequest_TransfersFunds() {
        Card fromCard = createTestCard();
        Card toCard = createTestCard();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));

        TransactionRequest request = new TransactionRequest(1L, 2L, BigDecimal.valueOf(300), "Test transfer");
        UserDto requester = new UserDto(1L, "user", Role.USER, true);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return transaction;
        });
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");

        TransactionDto result = transactionService.transfer(request, requester);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(700), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(800), toCard.getBalance());
        verify(cardRepository, times(2)).findById(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        Card fromCard = createTestCard();
        Card toCard = createTestCard();
        toCard.setId(2L);

        TransactionRequest request = new TransactionRequest(1L, 2L, BigDecimal.valueOf(1500), "Test transfer");
        UserDto requester = new UserDto(1L, "user", Role.USER, true);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.transfer(request, requester));

        verify(cardRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(cardRepository, transactionRepository);
    }

    @Test
    void transfer_UnauthorizedAccess_ThrowsException() {
        Card fromCard = createTestCard();
        Card toCard = createTestCard();
        toCard.setId(2L);

        TransactionRequest request = new TransactionRequest(1L, 2L, BigDecimal.valueOf(300), "Test transfer");
        UserDto requester = new UserDto(999L, "hacker", Role.USER, true);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(AccessDeniedException.class, () ->
                transactionService.transfer(request, requester));

        verify(cardRepository, times(2)).findById(anyLong());
        verifyNoMoreInteractions(cardRepository, transactionRepository);
    }

    @Test
    void getByCardId_AdminAccess_ReturnsTransactions() {
        Card card = createTestCard();
        UserDto admin = new UserDto(999L, "admin", Role.ADMIN, true);
        Transaction transaction = new Transaction();
        transaction.setFrom(card);
        transaction.setTo(card);
        Page<Transaction> page = new PageImpl<>(List.of(transaction));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(transactionRepository.findByFromIdOrToId(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        Page<TransactionDto> result = transactionService.getByCardId(1L, admin, Pageable.unpaged());

        assertNotNull(result);
        verify(cardRepository).findById(1L);
        verify(transactionRepository).findByFromIdOrToId(eq(1L), eq(1L), any(Pageable.class));
    }

    private Card createTestCard() {
        Card card = new Card();
        card.setId(1L);
        card.setNumber("1234567812345678");
        card.setHolder("John Doe");
        card.setExpiryDate(LocalDate.now().plusYears(1));
        card.setStatus(Card.Status.ACTIVE);
        card.setBalance(BigDecimal.valueOf(1000));
        card.setDeleted(false);

        User user = new User();
        user.setId(1L);
        card.setUser(user);

        return card;
    }
}
