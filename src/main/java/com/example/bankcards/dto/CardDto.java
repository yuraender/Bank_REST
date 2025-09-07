package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Schema(description = "DTO для представления банковской карты")
public class CardDto {

    @Schema(description = "ID карты")
    private final Long id;

    @Schema(description = "Номер карты")
    private final String number;

    @Schema(description = "Держатель карты")
    private final String holder;

    @Schema(description = "Дата окончания действия")
    private final LocalDate expiryDate;

    @Schema(description = "Статус карты")
    private final Card.Status status;

    @Schema(description = "Баланс карты")
    private final BigDecimal balance;

    @Schema(description = "Флаг удаления карты")
    private final boolean deleted;
}
