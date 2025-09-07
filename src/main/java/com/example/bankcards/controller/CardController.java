package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.CreateCardResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.PageableUtil;
import com.example.bankcards.util.ResponseUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Карты", description = "API для управления банковскими картами")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    @GetMapping("/own")
    @Operation(
            summary = "Получить собственные карты",
            description = "Возвращает список карт текущего пользователя с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт пользователя")
            }
    )
    public ResponseEntity<Page<CardDto>> own(
            @AuthenticationPrincipal User principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(cardService.getByUserId(principal.getId(), pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Получить все карты (только ADMIN)",
            description = "Возвращает список всех карт в системе с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список всех карт"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<Page<CardDto>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(cardService.getAll(pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить карту по ID (только ADMIN)",
            description = "Возвращает информацию о конкретной карте",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Информация о карте",
                            content = @Content(schema = @Schema(implementation = CardDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<CardDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(cardService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    @Operation(
            summary = "Создать новую карту (только ADMIN)",
            description = "Создает новую банковскую карту для пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Карта успешно создана",
                            content = @Content(schema = @Schema(implementation = CreateCardResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<CreateCardResponse> create(
            @RequestBody @Valid CreateCardRequest createCardRequest
    ) {
        return ResponseEntity.ok().body(cardService.create(createCardRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/activate")
    @Operation(
            summary = "Активировать карту (только ADMIN)",
            description = "Активирует указанную карту",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта активирована"),
                    @ApiResponse(responseCode = "400", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "400", description = "Карта удалена"),
                    @ApiResponse(responseCode = "400", description = "Карта истекла"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<?> activate(@PathVariable long id) {
        cardService.activate(id);
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card activated.");
    }

    @PostMapping("/{id}/block")
    @Operation(
            summary = "Заблокировать карту",
            description = "Блокирует указанную карту",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта заблокирована"),
                    @ApiResponse(responseCode = "400", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "400", description = "Карта удалена"),
                    @ApiResponse(responseCode = "400", description = "Карта истекла"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<?> block(
            @AuthenticationPrincipal User principal,
            @PathVariable long id
    ) {
        cardService.block(id, principal.toDto());
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card blocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить карту (только ADMIN)",
            description = "Удаляет указанную карту из системы",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта удалена"),
                    @ApiResponse(responseCode = "400", description = "Карта не найдена"),
                    @ApiResponse(responseCode = "400", description = "Карта уже удалена"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<?> delete(@PathVariable long id) {
        cardService.delete(id);
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card deleted.");
    }
}
