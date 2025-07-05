package com.example.minet.service;

import com.example.minet.entities.Currency;
import com.example.minet.entities.PriceHistory;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.PriceHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

class PriceSyncServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @InjectMocks
    private PriceSyncService priceSyncService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePriceHistory_SavesPriceHistoryForCurrenciesWithPrice() {
        Currency currency1 = new Currency();
        currency1.setCurrencyId(1);
        currency1.setCurrencyName("BTC");
        currency1.setPrice(new BigDecimal("65000.50"));

        Currency currency2 = new Currency();
        currency2.setCurrencyId(2);
        currency2.setCurrencyName("ETH");
        currency2.setPrice(new BigDecimal("3500.25"));

        when(currencyRepository.findAll()).thenReturn(List.of(currency1, currency2));

        priceSyncService.updatePriceHistory();

        // Verify that save was called twice
        verify(priceHistoryRepository, times(2)).save(any(PriceHistory.class));
    }

    @Test
    void testUpdatePriceHistory_SkipsCurrencyWithNullPrice() {
        Currency currency1 = new Currency();
        currency1.setCurrencyId(1);
        currency1.setCurrencyName("BTC");
        currency1.setPrice(new BigDecimal("65000.50"));

        Currency currency2 = new Currency();
        currency2.setCurrencyId(2);
        currency2.setCurrencyName("DOGE");
        currency2.setPrice(null); // should be skipped

        when(currencyRepository.findAll()).thenReturn(List.of(currency1, currency2));

        priceSyncService.updatePriceHistory();

        // Only one save should happen
        verify(priceHistoryRepository, times(1)).save(any(PriceHistory.class));
    }
}
