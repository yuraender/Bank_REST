package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Ответ на аутентификацию")
public class AuthResponse {

    @Schema(description = "JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String token;
}
