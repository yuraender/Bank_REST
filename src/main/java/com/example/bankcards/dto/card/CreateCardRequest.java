package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CreateCardRequest {

    @NotBlank
    @Size(max = 100)
    private final String holder;
    @NotNull
    @Future
    @JsonProperty("expiry_date")
    private final LocalDate expiryDate;
    @NotNull
    private final Long user;
}
