package com.example.bankcards.exception.entity;

public class CardNotFoundException extends EntityNotFoundException {

    public CardNotFoundException() {
        super("Card");
    }
}
