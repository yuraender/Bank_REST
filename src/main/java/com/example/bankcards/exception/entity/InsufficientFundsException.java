package com.example.bankcards.exception.entity;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Insufficient funds");
    }
}
