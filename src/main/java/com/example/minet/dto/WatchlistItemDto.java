package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WatchlistItemDto {
    private Integer currencyId;
    private String currencyName;
    private String currencyCode;
    private BigDecimal currentPrice;
    private Double priceChange24h; // ✅ Use Double for percentage
}
