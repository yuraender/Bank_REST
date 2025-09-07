package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, AuthControllerTest.TestSecurityConfig.class})
class AuthControllerTest {

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            .anyRequest().permitAll()
                    )
                    .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void auth_ValidCredentials_ReturnsToken() throws Exception {
        AuthRequest request = new AuthRequest("user", "password");
        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPassword");
        user.setEnabled(true);

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userDetailsService).loadUserByUsername("user");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtService).generateToken(user);
    }

    @Test
    void auth_InvalidPassword_ReturnsNullToken() throws Exception {
        AuthRequest request = new AuthRequest("user", "wrongpassword");
        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedPassword");
        user.setEnabled(true);

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist());

        verify(userDetailsService).loadUserByUsername("user");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
        verifyNoInteractions(jwtService);
    }

    @Test
    void auth_UserNotFound_ReturnsNullToken() throws Exception {
        AuthRequest request = new AuthRequest("nonexistent", "password");

        when(userDetailsService.loadUserByUsername("nonexistent")).thenReturn(null);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist());

        verify(userDetailsService).loadUserByUsername("nonexistent");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }
}
