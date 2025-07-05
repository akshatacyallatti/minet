package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseRequestDto {
    private Integer userId;
    private Integer currencyId;
    private BigDecimal amountInUsd;
    private String paymentMethod; // e.g. "VISA", "Wallet"
    private String currencyCode;
}

