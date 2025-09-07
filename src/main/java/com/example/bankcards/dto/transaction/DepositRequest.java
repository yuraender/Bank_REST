package com.example.bankcards.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на пополнение счета")
public class DepositRequest {

    @NotNull
    @Schema(description = "ID карты для пополнения", example = "1")
    private final Long card;

    @NotNull
    @Positive
    @Schema(description = "Сумма пополнения", example = "1000.00")
    private final BigDecimal amount;
}
