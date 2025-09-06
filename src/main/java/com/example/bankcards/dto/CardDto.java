package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class CardDto {

    private final Long id;
    private final String number;
    private final String holder;
    private final LocalDate expiryDate;
    private final Card.Status status;
    private final BigDecimal balance;
    private final boolean deleted;
}
