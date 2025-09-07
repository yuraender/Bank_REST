package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.transaction.DepositRequest;
import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.entity.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public Page<TransactionDto> getByCardId(Long cardId, UserDto requester, Pageable pageable) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);
        if (requester.getRole() != Role.ADMIN && !card.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You are not an owner of this card");
        }
        return transactionRepository.findByFromIdOrToId(cardId, cardId, pageable).map(Transaction::toDto);
    }

    public Page<TransactionDto> getByUserId(Long userId, UserDto requester, Pageable pageable) {
        if (requester.getRole() != Role.ADMIN && !userId.equals(requester.getId())) {
            throw new AccessDeniedException("You have no access to this user");
        }
        return transactionRepository.findByUserId(userId, pageable).map(Transaction::toDto);
    }

    @Transactional
    public TransactionDto deposit(DepositRequest depositRequest) {
        Card card = cardRepository.findById(depositRequest.getCard())
                .orElseThrow(CardNotFoundException::new);
        validateCard(card.toDto(encryptionService));

        card.setBalance(card.getBalance().add(depositRequest.getAmount()));
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setFrom(card);
        transaction.setTo(card);
        transaction.setAmount(depositRequest.getAmount());
        transaction.setComment("");
        transaction.setDate(LocalDateTime.now());

        Transaction createdTransaction = transactionRepository.save(transaction);
        return createdTransaction.toDto();
    }

    @Transactional
    public TransactionDto transfer(TransactionRequest transactionRequest, UserDto requester) {
        Card from = cardRepository.findById(transactionRequest.getFrom())
                .orElseThrow(CardNotFoundException::new);
        Card to = cardRepository.findById(transactionRequest.getTo())
                .orElseThrow(CardNotFoundException::new);

        if (!from.getUser().getId().equals(requester.getId())
                || !to.getUser().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You are not an owner of these cards");
        }
        validateCard(from.toDto(encryptionService));
        validateCard(to.toDto(encryptionService));
        if (from.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        from.setBalance(from.getBalance().subtract(transactionRequest.getAmount()));
        cardRepository.save(from);
        to.setBalance(to.getBalance().add(transactionRequest.getAmount()));
        cardRepository.save(to);

        Transaction transaction = new Transaction();
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setComment(transactionRequest.getComment());
        transaction.setDate(LocalDateTime.now());

        Transaction createdTransaction = transactionRepository.save(transaction);
        return createdTransaction.toDto();
    }

    private void validateCard(CardDto card) {
        if (card.isDeleted()) {
            throw new CardDeletedException(card);
        }
        if (card.getStatus() == Card.Status.EXPIRED) {
            throw new CardExpiredException(card);
        }
        if (card.getStatus() == Card.Status.BLOCKED) {
            throw new CardBlockedException(card);
        }
    }
}
