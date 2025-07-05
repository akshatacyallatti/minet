package com.example.minet.controllers;

import com.example.minet.dto.WatchlistRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Watchlist;
import com.example.minet.service.WatchlistService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WatchlistService watchlistService;

    @Autowired
    private ObjectMapper objectMapper;

    private WatchlistRequestDto getSampleDto() {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        dto.setUserId(1);
        dto.setCurrencyId(2);
        return dto;
    }

    private Watchlist getSampleWatchlist() {
        Watchlist w = new Watchlist();
        w.setId(1);
        w.setAddedAt(LocalDateTime.now());

        User user = new User();
        user.setUserId(1);
        user.setName("Test User");
        w.setUser(user);

        Currency currency = new Currency();
        currency.setCurrencyId(2);
        currency.setCurrencyName("BTC");
        w.setCurrency(currency);

        return w;
    }

    @Test
    void testToggleWatchlist_AddOrRemove() throws Exception {
        WatchlistRequestDto dto = getSampleDto();

        Mockito.when(watchlistService.toggleWatchlist(Mockito.any()))
                .thenReturn("Currency added to watchlist");

        mockMvc.perform(post("/api/v1/watchlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Currency added to watchlist"));
    }

    @Test
    void testGetWatchlist_Success() throws Exception {
        Watchlist watchlist = getSampleWatchlist();

        Mockito.when(watchlistService.getWatchlistByUser(1))
                .thenReturn(List.of(watchlist));

        mockMvc.perform(get("/api/v1/watchlist")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWatchlist_Empty() throws Exception {
        Mockito.when(watchlistService.getWatchlistByUser(1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/watchlist")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
