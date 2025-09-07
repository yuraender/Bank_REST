package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.CreateCardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.entity.CardDeletedException;
import com.example.bankcards.exception.entity.CardNotFoundException;
import com.example.bankcards.exception.entity.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private CardService cardService;

    @Test
    void getById_ExistingCard_ReturnsCardDto() {
        Card card = createTestCard();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");

        CardDto result = cardService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cardRepository).findById(1L);
    }

    @Test
    void getById_NonExistingCard_ThrowsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getById(1L));
        verify(cardRepository).findById(1L);
    }

    @Test
    void create_ValidRequest_ReturnsCreateCardResponse() {
        User user = createTestUser();
        CreateCardRequest request = new CreateCardRequest("John Doe", LocalDate.now().plusYears(1), 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumberHash(anyString())).thenReturn(false);
        when(encryptionService.encrypt(anyString())).thenReturn("encrypted-card-number");
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(1L);
            return card;
        });

        CreateCardResponse result = cardService.create(request);

        assertNotNull(result);
        assertNotNull(result.getNumber());
        assertNotNull(result.getCardDto());
        verify(userRepository).findById(1L);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void create_NonExistingUser_ThrowsException() {
        CreateCardRequest request = new CreateCardRequest("John Doe", LocalDate.now().plusYears(1), 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.create(request));
        verify(userRepository).findById(1L);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void activate_ValidCard_ActivatesCard() {
        Card card = createTestCard();
        card.setStatus(Card.Status.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.activate(1L);

        assertEquals(Card.Status.ACTIVE, card.getStatus());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
    }

    @Test
    void activate_DeletedCard_ThrowsException() {
        Card card = createTestCard();
        card.setDeleted(true);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(encryptionService.decrypt(anyString())).thenReturn("1234567812345678");

        assertThrows(CardDeletedException.class, () -> cardService.activate(1L));
        verify(cardRepository).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void block_ValidCard_BlocksCard() {
        Card card = createTestCard();
        UserDto requester = new UserDto(1L, "admin", Role.ADMIN, true);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.block(1L, requester);

        assertEquals(Card.Status.BLOCKED, card.getStatus());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
    }

    @Test
    void delete_ValidCard_MarksAsDeleted() {
        Card card = createTestCard();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.delete(1L);

        assertTrue(card.isDeleted());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
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

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole(Role.USER);
        return user;
    }
}
