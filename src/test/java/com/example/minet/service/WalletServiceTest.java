package com.example.minet.service;

import com.example.minet.dto.WalletCreateDto;
import com.example.minet.dto.WalletResponseDto;
import com.example.minet.entities.*;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.DuplicateWalletException;
import com.example.minet.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet_Success() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");
        dto.setBalance(BigDecimal.TEN);

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.empty());

        User user = new User();
        user.setUserId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Currency currency = new Currency();
        currency.setCurrencyId(101);
        currency.setCurrencyName("Bitcoin");
        when(currencyRepository.findById(101)).thenReturn(Optional.of(currency));

        Wallet savedWallet = new Wallet();
        savedWallet.setUser(user);
        savedWallet.setCurrency(currency);
        savedWallet.setBalance(BigDecimal.TEN);
        savedWallet.setCurrencyCode("BTC");
        savedWallet.setCurrencyName("Bitcoin");
        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        Wallet result = walletService.createWallet(dto);
        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getBalance());
        assertEquals("BTC", result.getCurrencyCode());
    }

    @Test
    void testCreateWallet_DuplicateWalletException() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.of(new Wallet()));

        assertThrows(DuplicateWalletException.class, () -> walletService.createWallet(dto));
    }

    @Test
    void testCreateWallet_UserNotFound() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> walletService.createWallet(dto));
    }

    @Test
    void testCreateWallet_CurrencyNotFound() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(currencyRepository.findById(101)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> walletService.createWallet(dto));
    }

    @Test
    void testUpdateWalletBalance_Success() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");
        dto.setBalance(BigDecimal.valueOf(100));

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet updated = walletService.updateWalletBalance(dto);
        assertEquals(BigDecimal.valueOf(100), updated.getBalance());
    }

    @Test
    void testUpdateWalletBalance_WalletNotFound() {
        WalletCreateDto dto = new WalletCreateDto();
        dto.setUserId(1);
        dto.setCurrencyId(101);
        dto.setCurrencyCode("BTC");

        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 101, "BTC"))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> walletService.updateWalletBalance(dto));
    }

    @Test
    void testGetWalletForUserAndCurrency_Success() {
        int userId = 1;
        Currency currency = new Currency();
        currency.setCurrencyId(101);
        currency.setCurrencyName("Bitcoin");
        currency.setCurrencyCode("BTC");
        currency.setSymbol("₿");
        currency.setPrice(BigDecimal.valueOf(30000));
        currency.setMarketCap(BigDecimal.valueOf(1000000));

        Wallet wallet = new Wallet();
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.valueOf(0.5));

        Purchase p1 = new Purchase();
        p1.setCryptoPurchased(BigDecimal.valueOf(0.1));
        Purchase p2 = new Purchase();
        p2.setCryptoPurchased(BigDecimal.valueOf(0.2));

        when(currencyRepository.findByCurrencyCode("BTC")).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(userId, "BTC")).thenReturn(Optional.of(wallet));
        when(purchaseRepository.findTop10ByUser_UserIdAndCurrency_CurrencyCodeOrderByPurchasedAtDesc(userId, "BTC"))
                .thenReturn(List.of(p1, p2));

        WalletResponseDto dto = walletService.getWalletForUserAndCurrency(userId, "BTC");
        assertEquals(2, dto.getTransactions().size());
        assertEquals(BigDecimal.valueOf(0.5), dto.getTotalBalance());
    }

    @Test
    void testGetWalletForUserAndCurrency_CurrencyNotFound() {
        when(currencyRepository.findByCurrencyCode("BTC")).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> walletService.getWalletForUserAndCurrency(1, "BTC"));
    }

    @Test
    void testGetWalletForUserAndCurrency_WalletNotFound() {
        Currency currency = new Currency();
        currency.setCurrencyId(101);
        currency.setCurrencyCode("BTC");
        currency.setCurrencyName("Bitcoin");

        when(currencyRepository.findByCurrencyCode("BTC")).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(1, "BTC")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> walletService.getWalletForUserAndCurrency(1, "BTC"));
    }
}