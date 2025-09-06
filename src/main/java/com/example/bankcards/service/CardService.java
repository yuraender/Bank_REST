package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.CreateCardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.entity.CardDeletedException;
import com.example.bankcards.exception.entity.CardExpiredException;
import com.example.bankcards.exception.entity.CardNotFoundException;
import com.example.bankcards.exception.entity.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtil;
import com.example.bankcards.util.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public Page<CardDto> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable).map(Card::toDto);
    }

    public CardDto getById(Long id) {
        return cardRepository.findById(id)
                .map(Card::toDto)
                .orElseThrow(CardNotFoundException::new);
    }

    public List<CardDto> getByUserId(Long userId) {
        return cardRepository.findByUserId(userId)
                .stream()
                .map(Card::toDto)
                .toList();
    }

    @Transactional
    public CreateCardResponse create(CreateCardRequest createCardRequest) {
        User user = userRepository.findById(createCardRequest.getUser())
                .orElseThrow(UserNotFoundException::new);

        String number, numberHash;
        do {
            number = CardUtil.generate();
            numberHash = CardUtil.hash(number);
        } while (cardRepository.existsByNumberHash(numberHash));

        Card card = new Card();
        card.setNumber(encryptionService.encrypt(number));
        card.setNumberHash(numberHash);
        card.setHolder(createCardRequest.getHolder());
        card.setExpiryDate(createCardRequest.getExpiryDate());
        card.setStatus(Card.Status.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        Card createdCard = cardRepository.save(card);
        return new CreateCardResponse(number, createdCard.toDto());
    }

    @Transactional
    public CardDto activate(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (card.isDeleted()) {
            throw new CardDeletedException();
        }
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException();
        }

        card.setStatus(Card.Status.ACTIVE);
        cardRepository.save(card);

        return card.toDto();
    }

    @Transactional
    public CardDto block(Long id, User requester) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (card.isDeleted()) {
            throw new CardDeletedException();
        }
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException();
        }
        if (requester.getRole() != Role.ADMIN || !card.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You are not an owner of this card");
        }

        card.setStatus(Card.Status.BLOCKED);
        cardRepository.save(card);

        return card.toDto();
    }

    @Transactional
    public CardDto delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
        card.setDeleted(true);
        cardRepository.save(card);
        return card.toDto();
    }
}
