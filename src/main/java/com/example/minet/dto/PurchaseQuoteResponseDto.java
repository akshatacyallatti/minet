package com.example.minet.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseQuoteResponseDto {
    private BigDecimal cryptoAmount;  // e.g. 0.0234510
    private BigDecimal rate;          // e.g. 34060069.54 (1 BTC)
    private BigDecimal fee;           // e.g. 0.001 BTC
    private BigDecimal totalUsd;      // e.g. 35000.00
}

