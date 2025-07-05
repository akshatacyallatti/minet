package com.example.minet.controllers;

import com.example.minet.dto.TradeResponseDto;
import com.example.minet.service.TradeService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TradeService tradeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetFilteredTrades_WithQueryParams() throws Exception {
        TradeResponseDto dto = new TradeResponseDto();
        dto.setCurrencyName("Bitcoin");
        dto.setPrice("50000");
        dto.setChangePercent(1.5);
        dto.setMarketCap(BigDecimal.valueOf(10000));
        dto.setAmount("0.1");
        dto.setTradeTime("2024-01-01T12:00:00");

        Mockito.when(tradeService.getFilteredTrades("7d", "BTC"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/trades")
                        .param("range", "7d")
                        .param("query", "BTC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].currencyName", is("Bitcoin")))
                .andExpect(jsonPath("$[0].price", is("50000")));
    }

    @Test
    void testGetFilteredTrades_WithoutQueryParams() throws Exception {
        Mockito.when(tradeService.getFilteredTrades(null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
