package com.example.bankcards.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EntityException extends RuntimeException {

    protected final String entity;
}
