package com.example.minet.service;

import com.example.minet.entities.Currency;
import com.example.minet.entities.PriceHistory;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceSyncService {

    private final CurrencyRepository currencyRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void updatePriceHistory() {
        List<Currency> currencies = currencyRepository.findAll();
       //TODO later we can fetch from external API to update the dynamically
        for (Currency currency : currencies) {
            BigDecimal currentPrice = currency.getPrice();

            if (currentPrice != null) {
                PriceHistory history = new PriceHistory();
                history.setCurrency(currency);
                history.setPrice(currentPrice);
                history.setTimestamp(LocalDateTime.now());
                priceHistoryRepository.save(history);
            }
        }
    }
}

