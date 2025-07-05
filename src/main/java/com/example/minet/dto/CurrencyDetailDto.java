package com.example.minet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class     CurrencyDetailDto {
    private Integer currencyId;
    private String currencyName;
    private String currencyCode;
    private Double changePercent;
    private String symbol;
    private BigDecimal currentPrice;
    private BigDecimal marketCap;
    private String volume24h;
    private String circulatingSupply;
    private String description;
    private boolean inWatchlist;
    private List<PricePoint> chart;
    private List<CorrelationItem> correlatedCurrencies;

    @Data
    public static class PricePoint {
        private LocalDateTime timestamp;
        private BigDecimal price;
        public PricePoint(LocalDateTime timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }
    }

    @Data
    public static class CorrelationItem {
        private String currencyName;
        private BigDecimal currentPrice;
        private int correlationPercent; // 0–100%
    }
}
