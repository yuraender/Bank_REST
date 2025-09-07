package com.example.bankcards.exception.entity;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.exception.EntityException;

public class CardDeletedException extends EntityException {

    private final CardDto card;

    public CardDeletedException(CardDto card) {
        super("Card");
        this.card = card;
    }

    @Override
    public String getMessage() {
        return String.format("Card %s is deleted", card.getNumber());
    }
}
