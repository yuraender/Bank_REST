package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.transaction.DepositRequest;
import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.PageableUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/own")
    public ResponseEntity<Page<TransactionDto>> own(
            @AuthenticationPrincipal User principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(transactionService.getByUserId(principal.getId(), principal.toDto(), pageable));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<Page<TransactionDto>> getByCard(
            @AuthenticationPrincipal User principal,
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(transactionService.getByCardId(cardId, principal.toDto(), pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransactionDto>> getByUser(
            @AuthenticationPrincipal User principal,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(transactionService.getByUserId(userId, principal.toDto(), pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> createDeposit(
            @RequestBody @Valid DepositRequest depositRequest
    ) {
        return ResponseEntity.ok().body(transactionService.deposit(depositRequest));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transfer(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        return ResponseEntity.ok().body(transactionService.transfer(transactionRequest, principal.toDto()));
    }
}
