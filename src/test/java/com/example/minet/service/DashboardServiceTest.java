package com.example.minet.service;

import com.example.minet.dto.DashboardResponseDto;
import com.example.minet.entities.*;
import com.example.minet.entities.Currency;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.TradeRepository;
import com.example.minet.repositories.WalletRepository;
import com.example.minet.repositories.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock private TradeRepository tradeRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private WatchlistRepository watchlistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Currency getCurrency() {
        Currency currency = new Currency();
        currency.setCurrencyId(1);
        currency.setCurrencyName("Bitcoin");
        currency.setCurrencyCode("BTC");
        currency.setPrice(BigDecimal.valueOf(100));
        currency.setChangePercent(2.5);
        return currency;
    }

    private Wallet getWallet(BigDecimal balance, Currency currency) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(currency);
        wallet.setCurrencyName(currency.getCurrencyName());
        wallet.setCurrencyCode(currency.getCurrencyCode());
        wallet.setBalance(balance);
        return wallet;
    }

    private Watchlist getWatchlist(Currency currency) {
        Watchlist watchlist = new Watchlist();
        watchlist.setCurrency(currency);
        return watchlist;
    }

    private Trade getTrade(int userId, Currency currency) {
        Trade trade = new Trade();
        trade.setTradeId(1);
        trade.setCurrency(currency);
        trade.setCurrencyName(currency.getCurrencyName());
        trade.setAmount(BigDecimal.ONE);
        trade.setPrice(currency.getPrice());
        trade.setCreatedAt(LocalDateTime.now());
        User buyer = new User();
        buyer.setUserId(userId);
        User seller = new User();
        seller.setUserId(2);
        trade.setBuyer(buyer);
        trade.setSeller(seller);
        return trade;
    }

    @Test
    void testGetDashboardForUser_success() {
        int userId = 1;
        Currency currency = getCurrency();
        Wallet wallet = getWallet(BigDecimal.TEN, currency);
        Watchlist watchlist = getWatchlist(currency);
        Trade trade = getTrade(userId, currency);

        when(watchlistRepository.findByUser_UserId(userId)).thenReturn(List.of(watchlist));
        when(walletRepository.findByUser_UserId(userId)).thenReturn(List.of(wallet));
        when(tradeRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId)).thenReturn(List.of(trade));

        DashboardResponseDto response = dashboardService.getDashboardForUser(userId);

        assertNotNull(response);
        assertEquals(1, response.getWatchlist().size());
        assertEquals(1, response.getWallets().size());
        assertEquals(1, response.getPortfolio().getCurrencies().size());
        assertEquals(1, response.getRecentTransactions().size());
    }

    @Test
    void testGetDashboardForUser_invalidUserId() {
        assertThrows(InvalidRequestException.class, () -> dashboardService.getDashboardForUser(null));
        assertThrows(InvalidRequestException.class, () -> dashboardService.getDashboardForUser(0));
    }

    @Test
    void testGetDashboardForUser_emptyWatchlist() {
        when(watchlistRepository.findByUser_UserId(anyInt())).thenReturn(Collections.emptyList());
        assertThrows(DataNotFoundException.class, () -> dashboardService.getDashboardForUser(1));
    }

    @Test
    void testGetDashboardForUser_emptyWallets() {
        int userId = 1;
        Currency currency = getCurrency();
        Watchlist watchlist = getWatchlist(currency);

        when(watchlistRepository.findByUser_UserId(userId)).thenReturn(List.of(watchlist));
        when(walletRepository.findByUser_UserId(userId)).thenReturn(Collections.emptyList());

        assertThrows(DataNotFoundException.class, () -> dashboardService.getDashboardForUser(userId));
    }

    @Test
    void testGetDashboardForUser_walletWithNullCurrency_skippedInPortfolio() {
        int userId = 1;
        Currency currency = getCurrency();
        Watchlist watchlist = getWatchlist(currency);

        Wallet walletWithNullCurrency = new Wallet();
        walletWithNullCurrency.setCurrency(null);
        walletWithNullCurrency.setCurrencyName("BTC");
        walletWithNullCurrency.setBalance(BigDecimal.TEN);

        when(watchlistRepository.findByUser_UserId(userId)).thenReturn(List.of(watchlist));
        when(walletRepository.findByUser_UserId(userId)).thenReturn(List.of(walletWithNullCurrency));
        when(tradeRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId)).thenReturn(Collections.emptyList());

        DashboardResponseDto response = dashboardService.getDashboardForUser(userId);

        assertNotNull(response);
        assertEquals(1, response.getWatchlist().size());
        assertEquals(1, response.getWallets().size());
        assertNotNull(response.getPortfolio());
        assertEquals(BigDecimal.ZERO, response.getPortfolio().getTotalBalance());
        assertEquals(0, response.getPortfolio().getCurrencies().size());
    }

    @Test
    void testGetDashboardForUser_emptyRecentTransactions() {
        int userId = 1;
        Currency currency = getCurrency();
        Watchlist watchlist = getWatchlist(currency);
        Wallet wallet = getWallet(BigDecimal.TEN, currency);

        when(watchlistRepository.findByUser_UserId(userId)).thenReturn(List.of(watchlist));
        when(walletRepository.findByUser_UserId(userId)).thenReturn(List.of(wallet));
        when(tradeRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId)).thenReturn(Collections.emptyList());

        DashboardResponseDto response = dashboardService.getDashboardForUser(userId);
        assertNotNull(response);
        assertTrue(response.getRecentTransactions().isEmpty());
    }
}