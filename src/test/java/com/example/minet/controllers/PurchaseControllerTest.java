package com.example.minet.controllers;

import com.example.minet.dto.*;
import com.example.minet.service.PurchaseService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetQuote_Success() throws Exception {
        PurchaseQuoteRequestDto requestDto = new PurchaseQuoteRequestDto();
        requestDto.setCurrencyId(2);
        requestDto.setAmountInUsd(BigDecimal.valueOf(100.00));

        PurchaseQuoteResponseDto responseDto = new PurchaseQuoteResponseDto();
//        responseDto.s("BTC");
//        responseDto.setCurrencyCode("BTC");
//        responseDto.setExchangeRate(BigDecimal.valueOf(50000));
//        responseDto.setEstimatedAmount(BigDecimal.valueOf(0.002));

        Mockito.when(purchaseService.getQuote(any(PurchaseQuoteRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/purchase/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyName").value("BTC"))
                .andExpect(jsonPath("$.exchangeRate").value(50000))
                .andExpect(jsonPath("$.estimatedAmount").value(0.002));
    }

    @Test
    void testExecutePurchase_Success() throws Exception {
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setUserId(1);
        requestDto.setCurrencyId(2);
        requestDto.setAmountInUsd(BigDecimal.valueOf(200.00));

        PurchaseResponseDto responseDto = new PurchaseResponseDto();
//        responseDto.se(1234L);
//        responseDto.setCurrencyName("ETH");
//        responseDto.setAmountPurchased(BigDecimal.valueOf(0.1));
//        responseDto.setTotalCostUsd(BigDecimal.valueOf(200.00));

        Mockito.when(purchaseService.executePurchase(any(PurchaseRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1234))
                .andExpect(jsonPath("$.currencyName").value("ETH"))
                .andExpect(jsonPath("$.amountPurchased").value(0.1))
                .andExpect(jsonPath("$.totalCostUsd").value(200.00));
    }
}

