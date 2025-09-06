package com.example.bankcards.exception.entity;

import com.example.bankcards.exception.EntityException;

public class EntityNotFoundException extends EntityException {

    public EntityNotFoundException(String entity) {
        super(entity);
    }
}
