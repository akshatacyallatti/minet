package com.example.minet.controllers;

import com.example.minet.dto.WalletCreateDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Wallet;
import com.example.minet.service.WalletService;
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
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private WalletCreateDto getSampleDto() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(2);
        dto.setBalance(BigDecimal.valueOf(1000));
        return dto;
    }

    private Wallet getSampleWallet() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(1);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setCurrencyName("USD");
        wallet.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setUserId(1);
        user.setName("Test User");
        wallet.setUser(user);

        Currency currency = new Currency();
        currency.setCurrencyId(2);
        currency.setCurrencyName("USD");
        wallet.setCurrency(currency);

        return wallet;
    }

    @Test
    void testCreateWallet_Success() throws Exception {
        WalletCreateDto dto = getSampleDto();
        Wallet wallet = getSampleWallet();

        Mockito.when(walletService.createWallet(Mockito.any())).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currencyName", is("USD")))
                .andExpect(jsonPath("$.balance", is(1000)));
    }

    @Test
    void testUpdateWalletBalance_Success() throws Exception {
        WalletCreateDto dto = getSampleDto();
        Wallet wallet = getSampleWallet();
        wallet.setBalance(BigDecimal.valueOf(2000));

        Mockito.when(walletService.updateWalletBalance(Mockito.any())).thenReturn(wallet);

        mockMvc.perform(patch("/api/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(2000)));
    }
}
