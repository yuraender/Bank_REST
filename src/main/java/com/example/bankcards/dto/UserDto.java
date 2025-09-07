package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "DTO для представления пользователя")
public class UserDto {

    @Schema(description = "ID пользователя")
    private final Long id;

    @Schema(description = "Имя пользователя")
    private final String username;

    @Schema(description = "Роль пользователя")
    private final Role role;

    @Schema(description = "Флаг активности пользователя")
    private final boolean enabled;
}
