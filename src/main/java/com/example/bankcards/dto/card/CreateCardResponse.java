package com.example.bankcards.dto.card;

import com.example.bankcards.dto.CardDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateCardResponse {

    private final String number;
    @JsonProperty("card")
    private final CardDto cardDto;
}
