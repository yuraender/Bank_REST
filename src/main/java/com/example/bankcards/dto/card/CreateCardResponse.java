package com.example.bankcards.dto.card;

import com.example.bankcards.dto.CardDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Ответ на создание карты")
public class CreateCardResponse {

    @Schema(description = "Номер карты", example = "1234567812345678")
    private final String number;

    @JsonProperty("card")
    @Schema(description = "Информация о созданной карте")
    private final CardDto cardDto;
}
