package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeResponseDto {
    private String currencyName;
    private String price;         // Formatted like "$65,000"
    private Double changePercent; // Formatted like "2.5%"
    private BigDecimal marketCap;     // Formatted like "1.3T"
//    private String buyerName;
//    private String sellerName;
    private String amount;
    private String tradeTime;
}
