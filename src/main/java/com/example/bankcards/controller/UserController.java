package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
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
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для управления пользователями")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/me")
    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает информацию о текущем аутентифицированном пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Информация о пользователе",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
                    )
            }
    )
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok().body(principal.toDto());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Получить всех пользователей (только ADMIN)",
            description = "Возвращает список всех пользователей с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<Page<UserDto>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(userService.getAll(pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя по ID (только ADMIN)",
            description = "Возвращает информацию о конкретном пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Информация о пользователе",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/cards")
    @Operation(
            summary = "Получить карты пользователя (только ADMIN)",
            description = "Возвращает список карт указанного пользователя с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт пользователя"),
                    @ApiResponse(responseCode = "400", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<Page<CardDto>> getUserCards(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageableUtil.makePageable(page - 1, limit, direction, sort);
        return ResponseEntity.ok().body(cardService.getByUserId(id, pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    @Operation(
            summary = "Создать пользователя (только ADMIN)",
            description = "Создает нового пользователя в системе",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователь создан",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Пользователь уже существует"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        return ResponseEntity.ok().body(userService.create(createUserRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/enabled")
    @Operation(
            summary = "Включить/отключить пользователя (только ADMIN)",
            description = "Изменяет статус активности пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус пользователя изменен"),
                    @ApiResponse(responseCode = "400", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    public ResponseEntity<?> setEnabled(
            @PathVariable long id, @RequestParam boolean value
    ) {
        userService.setEnabled(id, value);
        return ResponseUtil.buildMessage(HttpStatus.OK, value ? "User enabled." : "User disabled.");
    }
}
