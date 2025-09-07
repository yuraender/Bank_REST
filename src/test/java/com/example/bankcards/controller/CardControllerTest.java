package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.CreateCardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@ContextConfiguration(classes = {CardController.class, CardControllerTest.TestSecurityConfig.class})
class CardControllerTest {

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
    private CardService cardService;

    @Test
    void getById_ValidId_ReturnsCard() throws Exception {
        CardDto cardDto = new CardDto(
                1L,
                "1234567812345678",
                "John Doe",
                LocalDate.now().plusYears(1),
                Card.Status.ACTIVE,
                BigDecimal.valueOf(1000),
                false
        );

        when(cardService.getById(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("1234567812345678"))
                .andExpect(jsonPath("$.holder").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(1000));

        verify(cardService).getById(1L);
    }

    @Test
    void getAll_ReturnsCardsPage() throws Exception {
        CardDto cardDto = new CardDto(
                1L,
                "1234567812345678",
                "John Doe",
                LocalDate.now().plusYears(1),
                Card.Status.ACTIVE,
                BigDecimal.valueOf(1000),
                false
        );
        Page<CardDto> page = new PageImpl<>(List.of(cardDto));

        when(cardService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/cards")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("sort", "id")
                        .param("direction", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].number").value("1234567812345678"));

        verify(cardService).getAll(any());
    }

    @Test
    void create_ValidRequest_ReturnsCreatedCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest("John Doe", LocalDate.now().plusYears(1), 1L);
        CreateCardResponse response = new CreateCardResponse(
                "1234567812345678",
                new CardDto(
                        1L,
                        "1234567812345678",
                        "John Doe",
                        LocalDate.now().plusYears(1),
                        Card.Status.ACTIVE,
                        BigDecimal.ZERO,
                        false
                )
        );

        when(cardService.create(any(CreateCardRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("1234567812345678"))
                .andExpect(jsonPath("$.card.id").value(1));

        verify(cardService).create(any(CreateCardRequest.class));
    }

    @Test
    void activate_ValidId_ReturnsSuccess() throws Exception {
        doNothing().when(cardService).activate(1L);

        mockMvc.perform(post("/api/cards/1/activate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Card activated."));

        verify(cardService).activate(1L);
    }

    @Test
    void delete_ValidId_ReturnsSuccess() throws Exception {
        doNothing().when(cardService).delete(1L);

        mockMvc.perform(delete("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Card deleted."));

        verify(cardService).delete(1L);
    }
}
