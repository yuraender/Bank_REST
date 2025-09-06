package com.example.bankcards.exception.entity;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {
        super("User");
    }
}
