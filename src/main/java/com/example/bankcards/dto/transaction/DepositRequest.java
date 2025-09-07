package com.example.bankcards.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class DepositRequest {

    @NotNull
    private final Long card;
    @NotNull
    @Positive
    private final BigDecimal amount;
}
