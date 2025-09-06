package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDto {

    private final Long id;
    private final String username;
    private final Role role;
    private final boolean enabled;
}
