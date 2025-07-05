package com.example.minet.controllers;

import com.example.minet.dto.CurrencyDetailDto;
import com.example.minet.dto.CurrencyRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/currencies - Success with list")
    void testAddCurrencyList_Success() throws Exception {
        CurrencyRequestDto dto = new CurrencyRequestDto();
        dto.setCurrencyName("Bitcoin");
        dto.setCurrencyCode("BTC");
        dto.setSymbol("₿");
        dto.setPrice(BigDecimal.valueOf(10000));
        dto.setChangePercent(1.5);
        dto.setMarketCap(BigDecimal.valueOf(500000));

        Currency saved = new Currency();
        saved.setCurrencyId(2);
        saved.setCurrencyName("Bitcoin");
        saved.setCurrencyCode("BTC");

        when(currencyService.addCurrencyList(any())).thenReturn(
                Map.of(
                        "saved", List.of(saved),
                        "skipped", List.of("Currency already exists with code: ETH")
                )
        );

        mockMvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved[0].currencyId").value(2))
                .andExpect(jsonPath("$.saved[0].currencyName").value("Bitcoin"))
                .andExpect(jsonPath("$.skipped[0]").value("Currency already exists with code: ETH"));
    }

    @Test
    @DisplayName("PATCH /api/v1/currencies/{id} - Success")
    void testUpdateCurrency_Success() throws Exception {
        CurrencyRequestDto dto = new CurrencyRequestDto();
        dto.setCurrencyName("Ethereum");
        dto.setCurrencyCode("ETH");

        Currency updated = new Currency();
        updated.setCurrencyId(1);
        updated.setCurrencyName("Ethereum");

        when(currencyService.updateCurrency(eq(1), any(CurrencyRequestDto.class)))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/v1/currencies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyId").value(1))
                .andExpect(jsonPath("$.currencyName").value("Ethereum"));
    }

    @Test
    @DisplayName("GET /api/v1/currencies/{id}/details - Success")
    void testGetCurrencyDetails_Success() throws Exception {
        CurrencyDetailDto detailDto = new CurrencyDetailDto();
        detailDto.setCurrencyName("Bitcoin");

        when(currencyService.getCurrencyDetail(eq(1), eq("7d"), eq(1001)))
                .thenReturn(detailDto);

        mockMvc.perform(get("/api/v1/currencies/1/details")
                        .param("range", "7d")
                        .param("userId", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyName").value("Bitcoin"));
    }

    @Test
    @DisplayName("POST /api/v1/currencies - Invalid Request Body")
    void testAddCurrency_InvalidRequest() throws Exception {
        CurrencyRequestDto dto = new CurrencyRequestDto(); // Empty DTO

        when(currencyService.addCurrencyList(any())).thenThrow(new RuntimeException("Invalid input"));

        mockMvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isInternalServerError());
    }
}
