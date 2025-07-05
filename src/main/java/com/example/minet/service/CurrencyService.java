package com.example.minet.service;

import com.example.minet.dto.CurrencyDetailDto;
import com.example.minet.dto.CurrencyRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.PriceHistory;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.PriceHistoryRepository;
import com.example.minet.repositories.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final WatchlistRepository watchlistRepository;

    private static final int DEFAULT_CORRELATION_LIMIT = 4;

    public Map<String, Object> addCurrencyList(List<CurrencyRequestDto> currencyList) {
        List<Currency> savedCurrencies = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (CurrencyRequestDto dto : currencyList) {
            Optional<Currency> existing = currencyRepository.findByCurrencyCode(dto.getCurrencyCode());
            if (existing.isPresent()) {
                skipped.add("Currency already exists with code: " + dto.getCurrencyCode());
                continue;
            }

            Currency currency = new Currency();
            currency.setCurrencyName(dto.getCurrencyName());
            currency.setCurrencyCode(dto.getCurrencyCode());
            currency.setSymbol(dto.getSymbol());
            currency.setPrice(dto.getPrice());
            currency.setChangePercent(dto.getChangePercent());
            currency.setMarketCap(dto.getMarketCap());

            savedCurrencies.add(currencyRepository.save(currency));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("saved", savedCurrencies);
        response.put("skipped", skipped);
        return response;
    }

    public Currency updateCurrency(final Integer currencyId, final CurrencyRequestDto dto) {
        if (currencyId == null || dto == null) {
            throw new IllegalArgumentException("Currency ID and update data must not be null");
        }

        Currency currency = currencyRepository.findByCurrencyId(currencyId)
                .orElseThrow(() -> new DataNotFoundException("Currency not found"));

        currency.setCurrencyName(dto.getCurrencyName());
        currency.setCurrencyCode(dto.getCurrencyCode());
        currency.setSymbol(dto.getSymbol());
        currency.setPrice(dto.getPrice());
        currency.setChangePercent(dto.getChangePercent());
        currency.setMarketCap(dto.getMarketCap());

        return currencyRepository.save(currency);
    }


    public CurrencyDetailDto getCurrencyDetail(final Integer currencyId, final String range, final Integer userId) {
        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + currencyId));

        LocalDateTime from = getTimeFromRange(range);

        List<PriceHistory> priceHistoryList = priceHistoryRepository
                .findByCurrency_CurrencyIdAndTimestampAfterOrderByTimestampAsc(currencyId, from);

        if (priceHistoryList.isEmpty()) {
            throw new DataNotFoundException("No price history available for currency ID: " + currencyId);
        }

        List<CurrencyDetailDto.PricePoint> chart = priceHistoryList.stream()
                .map(ph -> new CurrencyDetailDto.PricePoint(ph.getTimestamp(), ph.getPrice()))
                .toList();

        List<CurrencyDetailDto.CorrelationItem> correlated = currencyRepository.findAll().stream()
                .filter(c -> !Objects.equals(c.getCurrencyId(), currencyId))
                .limit(DEFAULT_CORRELATION_LIMIT)
                .map(c -> {
                    CurrencyDetailDto.CorrelationItem item = new CurrencyDetailDto.CorrelationItem();
                    item.setCurrencyName(c.getCurrencyName());
                    item.setCurrentPrice(c.getPrice());
                    return item;
                })
                .toList();

        CurrencyDetailDto dto = new CurrencyDetailDto();
        dto.setCurrencyId(currency.getCurrencyId());
        dto.setCurrencyCode(currency.getCurrencyCode());
        dto.setCurrencyName(currency.getCurrencyName());
        dto.setSymbol(currency.getCurrencyCode());
        dto.setCurrentPrice(currency.getPrice());
        dto.setMarketCap(currency.getMarketCap());
        dto.setChart(chart);
        dto.setCorrelatedCurrencies(correlated);

        dto.setInWatchlist(userId != null &&
                watchlistRepository.existsByUser_UserIdAndCurrency_CurrencyId(userId, currencyId));

        return dto;
    }

    private LocalDateTime getTimeFromRange(final String range) {
        if (range == null) return LocalDateTime.now().minusMonths(1);
        log.info("range is:{}", range);
        return switch (range.toLowerCase()) {
            case "1h" -> LocalDateTime.now().minusHours(1);
            case "24h" -> LocalDateTime.now().minusHours(24);
            case "1w" -> LocalDateTime.now().minusWeeks(1);
            case "1m" -> LocalDateTime.now().minusMonths(1);
            case "1y" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.now().minusYears(10);
        };
    }
}
