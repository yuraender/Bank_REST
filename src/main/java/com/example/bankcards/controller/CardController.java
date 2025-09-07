package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.CreateCardResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
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
@RequestMapping(path = "/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/own")
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
    public ResponseEntity<CardDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(cardService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<CreateCardResponse> create(
            @RequestBody @Valid CreateCardRequest createCardRequest
    ) {
        return ResponseEntity.ok().body(cardService.create(createCardRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable long id) {
        cardService.activate(id);
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card activated.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/block")
    public ResponseEntity<?> block(
            @PathVariable long id, @AuthenticationPrincipal User principal
    ) {
        cardService.block(id, principal);
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card blocked.");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        cardService.delete(id);
        return ResponseUtil.buildMessage(HttpStatus.OK, "Card deleted.");
    }
}
