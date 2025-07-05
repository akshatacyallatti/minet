package com.example.minet.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "currency")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Integer currencyId;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "change_percent")
    private Double changePercent;

    @Column(name = "market_cap")
    private BigDecimal marketCap;
    // @OneToMany(mappedBy = "currency")
    // private List<Wallet> wallets;

}
