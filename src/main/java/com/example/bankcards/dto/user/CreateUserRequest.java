package com.example.bankcards.dto.user;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на создание пользователя")
public class CreateUserRequest {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Имя пользователя", example = "user")
    private final String username;

    @NotBlank
    @Schema(description = "Пароль", example = "user")
    private final String password;

    @NotNull
    @Schema(description = "Роль пользователя", example = "USER")
    private final Role role;
}
