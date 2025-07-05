package com.example.minet.service;

import com.example.minet.dto.TradeResponseDto;
import com.example.minet.entities.Trade;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public List<TradeResponseDto> getFilteredTrades(String range, String query) {
        LocalDateTime fromTime = getTimeFromRange(range);

        List<Trade> trades;

        if (query != null && !query.isBlank() && fromTime != null) {
            trades = tradeRepository.findByCurrencyNameContainingIgnoreCaseAndCreatedAtAfter(query, fromTime);
        } else if (query != null && !query.isBlank()) {
            trades = tradeRepository.findByCurrencyNameContainingIgnoreCase(query);
        } else if (fromTime != null) {
            trades = tradeRepository.findByCreatedAtAfter(fromTime);
        } else {
            trades = tradeRepository.findAll();
        }

        return trades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TradeResponseDto mapToDto(Trade trade) {
        TradeResponseDto dto = new TradeResponseDto();
        dto.setCurrencyName(trade.getCurrencyName());
        dto.setPrice(trade.getPrice() != null ? trade.getPrice().setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00");
        dto.setChangePercent(trade.getChangePercent());
        dto.setAmount(trade.getAmount() != null ? trade.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00");
        dto.setTradeTime(trade.getCreatedAt() != null ? trade.getCreatedAt().toString() : "");
        dto.setMarketCap(trade.getMarketCap());
        return dto;
    }

    private LocalDateTime getTimeFromRange(String range) {
        if (range == null || range.trim().isEmpty()) return null;

        return switch (range.toLowerCase()) {
            case "1h" -> LocalDateTime.now().minusHours(1);
            case "24h" -> LocalDateTime.now().minusHours(24);
            case "1w" -> LocalDateTime.now().minusWeeks(1);
            case "1m" -> LocalDateTime.now().minusMonths(1);
            case "1y" -> LocalDateTime.now().minusYears(1);
            case "all" -> null;
            default -> throw new InvalidRequestException("Invalid time range: " + range);
        };
    }
}
