package com.example.minet.repositories;

import com.example.minet.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByCurrencyId(Integer currencyId);
    Optional<Currency> findByCurrencyCode(String currencyCode);
    Optional<Currency> findBySymbol(String symbol);
}
