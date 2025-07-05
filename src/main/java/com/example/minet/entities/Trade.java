package com.example.minet.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Trade {
    @Id
    @GeneratedValue
    private Integer tradeId;

    @ManyToOne
    private User buyer;
    @ManyToOne
    private User seller;

    @ManyToOne
    private Currency currency;

    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime tradeTime;
    private LocalDateTime createdAt;
    private String currencyName;
    @Column(name = "change_percent")
    private Double changePercent;

    @Column(name = "market_cap")
    private BigDecimal marketCap;
}

