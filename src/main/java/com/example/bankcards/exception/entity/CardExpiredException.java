package com.example.bankcards.exception.entity;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.exception.EntityException;

public class CardExpiredException extends EntityException {

    private final CardDto card;

    public CardExpiredException(CardDto card) {
        super("Card");
        this.card = card;
    }

    @Override
    public String getMessage() {
        return String.format("Card %s is expired", card.getNumber());
    }
}
