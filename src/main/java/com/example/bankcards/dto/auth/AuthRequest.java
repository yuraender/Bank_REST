package com.example.bankcards.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthRequest {

    @NotBlank
    @Size(max = 50)
    private final String username;
    @NotBlank
    private final String password;
}
