package com.example.minet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class WalletResponseDto {

    private Integer currencyId;
    private String currencyCode;
    private String currencyName;
    private String symbol;
    private BigDecimal currentPrice;
    private Double changePercent;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private BigDecimal circulatingSupply;
    private boolean isInWatchlist;

    private BigDecimal totalBalance;
    private BigDecimal balanceInUsd;

    private List<TransactionItem> transactions;

    @Getter
    @Setter
    public static class TransactionItem {
        private String date; // Preferably format: "MMM dd"
        private String from;
        private String status; // e.g., "Purchased"
        private BigDecimal amount; // in BTC
        private BigDecimal valueInUsd;
    }
}
