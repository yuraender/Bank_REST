package com.example.bankcards.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class TransactionRequest {

    @NotNull
    private final Long from;
    @NotNull
    private final Long to;
    @NotNull
    @Positive
    private final BigDecimal amount;
    @NotNull
    @Size(max = 100)
    private final String comment;
}
