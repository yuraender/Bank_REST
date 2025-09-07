package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, UserControllerTest.TestSecurityConfig.class})
class UserControllerTest {

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
    private UserService userService;

    @MockitoBean
    private CardService cardService;

    @Test
    void get_ValidId_ReturnsUser() throws Exception {
        UserDto userDto = new UserDto(1L, "testuser", Role.USER, true);
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(userService).getById(1L);
    }

    @Test
    void getAll_ReturnsUsersPage() throws Exception {
        UserDto userDto = new UserDto(1L, "testuser", Role.USER, true);
        Page<UserDto> page = new PageImpl<>(List.of(userDto));

        when(userService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("sort", "id")
                        .param("direction", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));

        verify(userService).getAll(any());
    }

    @Test
    void create_ValidRequest_ReturnsCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newuser", "password", Role.USER);
        UserDto userDto = new UserDto(1L, "newuser", Role.USER, true);

        when(userService.create(any(CreateUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).create(any(CreateUserRequest.class));
    }

    @Test
    void setEnabled_ValidRequest_ReturnsSuccess() throws Exception {
        doNothing().when(userService).setEnabled(1L, true);

        mockMvc.perform(post("/api/users/1/enabled")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User enabled."));

        verify(userService).setEnabled(1L, true);
    }
}
