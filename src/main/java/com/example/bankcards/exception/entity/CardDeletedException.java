package com.example.bankcards.exception.entity;

import com.example.bankcards.exception.EntityException;

public class CardDeletedException extends EntityException {

    public CardDeletedException() {
        super("Card");
    }

    @Override
    public String getMessage() {
        return "Card is deleted";
    }
}
