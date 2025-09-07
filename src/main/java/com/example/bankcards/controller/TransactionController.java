package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.transaction.DepositRequest;
import com.example.bankcards.dto.transaction.TransactionRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Транзакции", description = "API для управления транзакциями")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/own")
    @Operation(
            summary = "Получить собственные транзакции",
            description = "Возвращает список транзакций текущего пользователя с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список транзакций пользователя")
            }
    )
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
    @Operation(
            summary = "Получить транзакции по карте",
            description = "Возвращает список транзакций для указанной карты",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список транзакций карты"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
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
    @Operation(
            summary = "Получить транзакции по пользователю",
            description = "Возвращает список транзакций для указанного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список транзакций пользователя"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
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
    @Operation(
            summary = "Пополнение счета (только ADMIN)",
            description = "Выполняет пополнение счета указанной карты",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Пополнение выполнено",
                            content = @Content(schema = @Schema(implementation = TransactionDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<TransactionDto> createDeposit(
            @RequestBody @Valid DepositRequest depositRequest
    ) {
        return ResponseEntity.ok().body(transactionService.deposit(depositRequest));
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Перевод средств",
            description = "Выполняет перевод средств между картами",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Перевод выполнен",
                            content = @Content(schema = @Schema(implementation = TransactionDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Недостаточно средств"),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    public ResponseEntity<TransactionDto> transfer(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        return ResponseEntity.ok().body(transactionService.transfer(transactionRequest, principal.toDto()));
    }
}
