package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthRequest authRequest
    ) {
        User user = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (user == null
                || !passwordEncoder.matches(authRequest.getPassword(), user.getPassword())
                || !user.isEnabled()) {
            return ResponseEntity.ok(new AuthResponse(null));
        }
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user)));
    }
}
