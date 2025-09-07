package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.entity.UserAlreadyExistsException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.PageableUtil;
import com.example.bankcards.util.ResponseUtil;
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
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok().body(principal.toDto());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
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
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/cards")
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
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        if (userService.existsByUsername(createUserRequest.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        return ResponseEntity.ok().body(userService.create(createUserRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/enabled")
    public ResponseEntity<?> setEnabled(
            @PathVariable long id, @RequestParam boolean value
    ) {
        userService.setEnabled(id, value);
        return ResponseUtil.buildMessage(HttpStatus.OK, value ? "User enabled." : "User disabled.");
    }
}
