package com.example.minet.service;

import com.example.minet.dto.WatchlistRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Watchlist;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.UserRepository;
import com.example.minet.repositories.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchlistServiceTest {

    @InjectMocks
    private WatchlistService watchlistService;

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    private User user;
    private Currency currency;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");

        currency = new Currency();
        currency.setCurrencyId(10);
        currency.setCurrencyCode("BTC");
    }

    @Test
    void testToggleWatchlist_AddsCurrency() {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        dto.setUserId(1);
        dto.setCurrencyId(10);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(10)).thenReturn(Optional.of(currency));
        when(watchlistRepository.findByUserAndCurrency(user, currency)).thenReturn(Optional.empty());

        String result = watchlistService.toggleWatchlist(dto);

        assertEquals("Currency added to watchlist", result);
        verify(watchlistRepository).save(any(Watchlist.class));
    }

    @Test
    void testToggleWatchlist_RemovesCurrency() {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        dto.setUserId(1);
        dto.setCurrencyId(10);
        Watchlist existing = new Watchlist();
        existing.setUser(user);
        existing.setCurrency(currency);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(10)).thenReturn(Optional.of(currency));
        when(watchlistRepository.findByUserAndCurrency(user, currency)).thenReturn(Optional.of(existing));

        String result = watchlistService.toggleWatchlist(dto);

        assertEquals("Currency removed from watchlist", result);
        verify(watchlistRepository).delete(existing);
    }

    @Test
    void testToggleWatchlist_UserNotFound() {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        dto.setUserId(99);
        dto.setCurrencyId(10);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> watchlistService.toggleWatchlist(dto));
    }

    @Test
    void testToggleWatchlist_CurrencyNotFound() {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        dto.setUserId(1);
        dto.setCurrencyId(999);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> watchlistService.toggleWatchlist(dto));
    }

    @Test
    void testGetWatchlistByUser_Success() {
        Watchlist watchlist1 = new Watchlist();
        watchlist1.setCurrency(currency);
        watchlist1.setUser(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(watchlistRepository.findAllByUser(user)).thenReturn(List.of(watchlist1));

        List<Watchlist> result = watchlistService.getWatchlistByUser(1);

        assertEquals(1, result.size());
        assertEquals(currency, result.get(0).getCurrency());
    }

    @Test
    void testGetWatchlistByUser_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> watchlistService.getWatchlistByUser(1));
    }
}
