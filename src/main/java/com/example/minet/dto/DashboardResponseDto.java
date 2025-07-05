package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DashboardResponseDto {
    private List<WatchlistItemDto> watchlist;
    private Portfolio portfolio;
    private List<WalletInfo> wallets;
    private List<RecentTransaction> recentTransactions;

    @Data
    public static class WatchlistItem {
        private Integer currencyId;
        private String currencyName;
        private String currencyCode;
        private BigDecimal currentPrice;
        private Double priceChange24h;
    }

    @Data
    public static class Portfolio {
        private BigDecimal totalBalance;
        private List<CurrencyHolding> currencies;
    }

    @Data
    public static class CurrencyHolding {
        private String currencyName;
        private BigDecimal amountHeld;
        private BigDecimal usdValue;
        private String  symbol;
    }

    @Data
    public static class WalletInfo {
        private String currency;
        private BigDecimal balance;
    }

    @Data
    public static class RecentTransaction {
        private Integer transactionId;
        private String type; // "BUY" or "SELL"
        private String currency;
        private BigDecimal amount;
        private BigDecimal valueUsd;
        private LocalDateTime timestamp;
    }
}





