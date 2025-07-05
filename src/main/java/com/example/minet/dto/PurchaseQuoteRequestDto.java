package com.example.minet.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseQuoteRequestDto {
    private Integer currencyId;       // e.g. BTC
    private BigDecimal amountInUsd;   // e.g. 34000
}

