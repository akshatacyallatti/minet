package com.example.minet.service;

import com.example.minet.dto.CurrencyDetailDto;
import com.example.minet.dto.CurrencyRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.PriceHistory;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.PriceHistoryRepository;
import com.example.minet.repositories.WatchlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private WatchlistRepository watchlistRepository;

    private CurrencyRequestDto getRequestDto() {
        CurrencyRequestDto dto = new CurrencyRequestDto();
        dto.setCurrencyCode("BTC");
        dto.setCurrencyName("Bitcoin");
        dto.setSymbol("₿");
        dto.setPrice(BigDecimal.valueOf(30000));
        dto.setChangePercent(1.5);
        dto.setMarketCap(BigDecimal.valueOf(500_000_000));
        return dto;
    }

    private Currency getCurrency() {
        Currency currency = new Currency();
        currency.setCurrencyId(1);
        currency.setCurrencyCode("BTC");
        currency.setCurrencyName("Bitcoin");
        currency.setSymbol("₿");
        currency.setPrice(BigDecimal.valueOf(30000));
        currency.setChangePercent(1.5);
        currency.setMarketCap(BigDecimal.valueOf(500_000_000));
        return currency;
    }

    @Test
    void testAddCurrencyList_whenNewCurrency_shouldSave() {
        CurrencyRequestDto dto = getRequestDto();
        when(currencyRepository.findByCurrencyCode("BTC")).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenReturn(getCurrency());

        Map<String, Object> result = currencyService.addCurrencyList(List.of(dto));

        assertEquals(1, ((List<?>) result.get("saved")).size());
        assertTrue(((List<?>) result.get("skipped")).isEmpty());
    }

    @Test
    void testAddCurrencyList_whenCurrencyAlreadyExists_shouldSkip() {
        CurrencyRequestDto dto = getRequestDto();
        when(currencyRepository.findByCurrencyCode("BTC")).thenReturn(Optional.of(getCurrency()));

        Map<String, Object> result = currencyService.addCurrencyList(List.of(dto));

        assertTrue(((List<?>) result.get("saved")).isEmpty());
        assertEquals(1, ((List<?>) result.get("skipped")).size());
    }

    @Test
    void testUpdateCurrency_success() {
        CurrencyRequestDto dto = getRequestDto();
        Currency existingCurrency = getCurrency();
        when(currencyRepository.findByCurrencyId(1)).thenReturn(Optional.of(existingCurrency));
        when(currencyRepository.save(any())).thenReturn(existingCurrency);

        Currency updated = currencyService.updateCurrency(1, dto);

        assertEquals("Bitcoin", updated.getCurrencyName());
        verify(currencyRepository).save(any());
    }

    @Test
    void testUpdateCurrency_nullIdOrDto_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> currencyService.updateCurrency(null, getRequestDto()));
        assertThrows(IllegalArgumentException.class, () -> currencyService.updateCurrency(1, null));
    }

    @Test
    void testUpdateCurrency_notFound_shouldThrow() {
        when(currencyRepository.findByCurrencyId(99)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> currencyService.updateCurrency(99, getRequestDto()));
    }

    @Test
    void testGetCurrencyDetail_success() {
        Currency currency = getCurrency();
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        PriceHistory ph = new PriceHistory();
        ph.setTimestamp(LocalDateTime.now());
        ph.setPrice(BigDecimal.valueOf(30000));
        when(priceHistoryRepository
                .findByCurrency_CurrencyIdAndTimestampAfterOrderByTimestampAsc(eq(1), any()))
                .thenReturn(List.of(ph));

        Currency correlated = getCurrency();
        correlated.setCurrencyId(2);
        correlated.setCurrencyCode("ETH");
        correlated.setCurrencyName("Ethereum");
        correlated.setPrice(BigDecimal.valueOf(2000));
        when(currencyRepository.findAll()).thenReturn(List.of(currency, correlated));

        when(watchlistRepository.existsByUser_UserIdAndCurrency_CurrencyId(10, 1)).thenReturn(true);

        CurrencyDetailDto result = currencyService.getCurrencyDetail(1, "1h", 10);

        assertEquals(1, result.getCurrencyId());
        assertEquals(1, result.getChart().size());
        assertEquals(1, result.getCorrelatedCurrencies().size());
    }

    @Test
    void testGetCurrencyDetail_currencyNotFound() {
        when(currencyRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class,
                () -> currencyService.getCurrencyDetail(100, "1h", 1));
    }

    @Test
    void testGetCurrencyDetail_priceHistoryEmpty_shouldThrow() {
        when(currencyRepository.findById(1)).thenReturn(Optional.of(getCurrency()));
        when(priceHistoryRepository
                .findByCurrency_CurrencyIdAndTimestampAfterOrderByTimestampAsc(eq(1), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(DataNotFoundException.class,
                () -> currencyService.getCurrencyDetail(1, "1h", 1));
    }

    @Test
    void testGetTimeFromRange_variousRanges() throws Exception {
        Method method = CurrencyService.class.getDeclaredMethod("getTimeFromRange", String.class);
        method.setAccessible(true);

        assertTrue(((LocalDateTime) method.invoke(currencyService, "1h")).isBefore(LocalDateTime.now()));
        assertTrue(((LocalDateTime) method.invoke(currencyService, "24h")).isBefore(LocalDateTime.now()));
        assertTrue(((LocalDateTime) method.invoke(currencyService, "1w")).isBefore(LocalDateTime.now()));
        assertTrue(((LocalDateTime) method.invoke(currencyService, "1m")).isBefore(LocalDateTime.now()));
        assertTrue(((LocalDateTime) method.invoke(currencyService, "1y")).isBefore(LocalDateTime.now()));
        assertTrue(((LocalDateTime) method.invoke(currencyService, "unknown")).isBefore(LocalDateTime.now().minusYears(9)));
    }

}
