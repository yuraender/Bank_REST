package com.example.bankcards.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на перевод средств")
public class TransactionRequest {

    @NotNull
    @Schema(description = "ID карты отправителя", example = "1")
    private final Long from;

    @NotNull
    @Schema(description = "ID карты получателя", example = "2")
    private final Long to;

    @NotNull
    @Positive
    @Schema(description = "Сумма перевода", example = "500.00")
    private final BigDecimal amount;

    @NotNull
    @Size(max = 100)
    @Schema(description = "Комментарий к переводу", example = "Оплата услуг")
    private final String comment;
}
