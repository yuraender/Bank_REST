package com.example.bankcards.exception.entity;

import com.example.bankcards.exception.EntityException;

public class EntityAlreadyExistException extends EntityException {

    public EntityAlreadyExistException(String entity) {
        super(entity);
    }
}
