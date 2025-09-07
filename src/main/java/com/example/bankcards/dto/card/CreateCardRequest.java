package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на создание карты")
public class CreateCardRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Держатель карты", example = "IVAN IVANOV")
    private final String holder;

    @NotNull
    @Future
    @JsonProperty("expiry_date")
    @Schema(description = "Дата окончания действия карты", example = "2025-12-31")
    private final LocalDate expiryDate;

    @NotNull
    @Schema(description = "ID владельца карты", example = "1")
    private final Long user;
}
