package com.example.minet.service;

import com.example.minet.dto.TradeResponseDto;
import com.example.minet.entities.Trade;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Trade createSampleTrade() {
        Trade trade = new Trade();
        trade.setCurrencyName("BTC");
        trade.setPrice(BigDecimal.valueOf(50000.123));
        trade.setAmount(BigDecimal.valueOf(0.5));
        trade.setChangePercent(1.25);
        trade.setCreatedAt(LocalDateTime.of(2024, 6, 1, 12, 0));
        trade.setMarketCap(BigDecimal.valueOf(1000000000));
        return trade;
    }

    @Test
    void testGetFilteredTrades_withQueryAndRange() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findByCurrencyNameContainingIgnoreCaseAndCreatedAtAfter(anyString(), any())).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades("24h", "BTC");
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getCurrencyName());
    }

    @Test
    void testGetFilteredTrades_withOnlyQuery() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findByCurrencyNameContainingIgnoreCase(anyString())).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades(null, "BTC");
        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredTrades_withOnlyRange() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findByCreatedAtAfter(any())).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades("1w", null);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredTrades_withNoQueryOrRange() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findAll()).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades(null, null);
        assertEquals(1, result.size());
    }

    @Test
    void testMapToDto_withNullValues() {
        Trade trade = new Trade();
        trade.setCurrencyName("BTC");
        trade.setCreatedAt(null);

        List<Trade> tradeList = List.of(trade);
        when(tradeRepository.findAll()).thenReturn(tradeList);

        List<TradeResponseDto> result = tradeService.getFilteredTrades(null, null);
        assertEquals("0.00", result.get(0).getAmount());
        assertEquals("0.00", result.get(0).getPrice());
        assertEquals("", result.get(0).getTradeTime());
    }

    @Test
    void testGetTimeFromRange_invalid() {
        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> tradeService.getFilteredTrades("invalid", "BTC"));
        assertEquals("Invalid time range: invalid", ex.getMessage());
    }

    @Test
    void testGetTimeFromRange_validInputs() {
        when(tradeRepository.findByCurrencyNameContainingIgnoreCaseAndCreatedAtAfter(anyString(), any())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> tradeService.getFilteredTrades("1h", "BTC"));
        assertDoesNotThrow(() -> tradeService.getFilteredTrades("24h", "BTC"));
        assertDoesNotThrow(() -> tradeService.getFilteredTrades("1w", "BTC"));
        assertDoesNotThrow(() -> tradeService.getFilteredTrades("1m", "BTC"));
        assertDoesNotThrow(() -> tradeService.getFilteredTrades("1y", "BTC"));
        assertDoesNotThrow(() -> tradeService.getFilteredTrades("all", "BTC"));
    }

    @Test
    void testGetFilteredTrades_withBlankRange() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findByCurrencyNameContainingIgnoreCase("BTC")).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades("   ", "BTC");
        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredTrades_withNullQueryAndBlankRange() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findAll()).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades("   ", null);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredTrades_withBlankQuery() {
        Trade trade = createSampleTrade();
        when(tradeRepository.findByCreatedAtAfter(any())).thenReturn(List.of(trade));

        List<TradeResponseDto> result = tradeService.getFilteredTrades("1w", "  ");
        assertEquals(1, result.size());
    }
}
