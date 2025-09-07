package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Запрос на аутентификацию")
public class AuthRequest {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Имя пользователя", example = "admin")
    private final String username;

    @NotBlank
    @Schema(description = "Пароль", example = "admin")
    private final String password;
}
