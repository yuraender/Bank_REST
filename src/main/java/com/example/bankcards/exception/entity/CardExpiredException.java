package com.example.bankcards.exception.entity;

import com.example.bankcards.exception.EntityException;

public class CardExpiredException extends EntityException {

    public CardExpiredException() {
        super("Card");
    }

    @Override
    public String getMessage() {
        return "Card is expired";
    }
}
