package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "DTO для представления транзакции")
public class TransactionDto {

    @Schema(description = "ID транзакции")
    private final Long id;

    @Schema(description = "ID карты отправителя")
    private final Long from;

    @Schema(description = "ID карты получателя")
    private final Long to;

    @Schema(description = "Сумма транзакции")
    private final BigDecimal amount;

    @Schema(description = "Комментарий к транзакции")
    private final String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата и время транзакции")
    private final LocalDateTime date;
}
