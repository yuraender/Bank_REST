package com.example.bankcards.exception.entity;

public class UserAlreadyExistsException extends EntityAlreadyExistException {

    public UserAlreadyExistsException() {
        super("User");
    }
}
