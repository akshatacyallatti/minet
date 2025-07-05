package com.example.minet.service;

import com.example.minet.dto.*;
import com.example.minet.entities.*;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock private CurrencyRepository currencyRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private AppLogRepository logRepository;
    @Mock private PurchaseRepository purchaseRepository;

    private Currency getCurrency() {
        Currency c = new Currency();
        c.setCurrencyId(1);
        c.setCurrencyCode("BTC");
        c.setCurrencyName("Bitcoin");
        c.setPrice(BigDecimal.valueOf(100));
        return c;
    }

    private Wallet getUsdWallet(BigDecimal balance) {
        Wallet w = new Wallet();
        User user = new User();
        user.setUserId(1);
        w.setUser(user);
        w.setCurrency(new Currency());
        w.setBalance(balance);
        return w;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetQuote_Success() {
        Currency c = getCurrency();
        when(currencyRepository.findById(1)).thenReturn(Optional.of(c));

        PurchaseQuoteRequestDto dto = new PurchaseQuoteRequestDto();
        dto.setCurrencyId(1);
        dto.setAmountInUsd(BigDecimal.valueOf(200));

        PurchaseQuoteResponseDto res = purchaseService.getQuote(dto);

        assertEquals("0.001", res.getFee().toPlainString());
        assertEquals(0, res.getRate().compareTo(BigDecimal.valueOf(100)));
        assertEquals(0, res.getCryptoAmount().compareTo(BigDecimal.valueOf(2)));
    }

    @Test
    void testGetQuote_InvalidAmount() {
        PurchaseQuoteRequestDto dto = new PurchaseQuoteRequestDto();
        dto.setCurrencyId(1);
        dto.setAmountInUsd(BigDecimal.ZERO);
        when(currencyRepository.findById(1)).thenReturn(Optional.of(getCurrency()));
        assertThrows(InvalidRequestException.class, () -> purchaseService.getQuote(dto));
    }

    @Test
    void testGetQuote_CurrencyNotFound() {
        when(currencyRepository.findById(1)).thenReturn(Optional.empty());
        PurchaseQuoteRequestDto dto = new PurchaseQuoteRequestDto();
        dto.setCurrencyId(1);
        dto.setAmountInUsd(BigDecimal.TEN);
        assertThrows(DataNotFoundException.class, () -> purchaseService.getQuote(dto));
    }

    @Test
    void testExecutePurchase_Success_ExistingWallet() {
        Currency currency = getCurrency();
        Wallet usdWallet = getUsdWallet(BigDecimal.valueOf(1000));
        Wallet cryptoWallet = new Wallet();
        cryptoWallet.setBalance(BigDecimal.ZERO);

        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setCurrencyId(1);
        dto.setCurrencyCode("BTC");
        dto.setUserId(1);
        dto.setAmountInUsd(BigDecimal.valueOf(100));

        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(1, "USD"))
                .thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 1, "BTC"))
                .thenReturn(Optional.of(cryptoWallet));

        PurchaseResponseDto res = purchaseService.executePurchase(dto);

        assertEquals("SUCCESS", res.getStatus());
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(logRepository).save(any(AppLog.class));
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void testExecutePurchase_CurrencyNotFound() {
        when(currencyRepository.findById(1)).thenReturn(Optional.empty());
        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setCurrencyId(1);
        dto.setAmountInUsd(BigDecimal.TEN);
        assertThrows(DataNotFoundException.class, () -> purchaseService.executePurchase(dto));
    }

    @Test
    void testExecutePurchase_InvalidAmount() {
        when(currencyRepository.findById(1)).thenReturn(Optional.of(getCurrency()));
        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setCurrencyId(1);
        dto.setAmountInUsd(BigDecimal.ZERO);
        assertThrows(InvalidRequestException.class, () -> purchaseService.executePurchase(dto));
    }

    @Test
    void testExecutePurchase_InsufficientUsdBalance() {
        Currency currency = getCurrency();
        Wallet usdWallet = getUsdWallet(BigDecimal.valueOf(50));

        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(1, "USD"))
                .thenReturn(Optional.of(usdWallet));

        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setCurrencyId(1);
        dto.setCurrencyCode("BTC");
        dto.setUserId(1);
        dto.setAmountInUsd(BigDecimal.valueOf(100));

        assertThrows(InvalidRequestException.class, () -> purchaseService.executePurchase(dto));
    }

    @Test
    void testExecutePurchase_NewCryptoWalletCreated() {
        Currency currency = getCurrency();
        Wallet usdWallet = getUsdWallet(BigDecimal.valueOf(1000));

        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyCode(1, "USD"))
                .thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUser_UserIdAndCurrency_CurrencyIdAndCurrencyCode(1, 1, "BTC"))
                .thenReturn(Optional.empty());

        PurchaseRequestDto dto = new PurchaseRequestDto();
        dto.setCurrencyId(1);
        dto.setCurrencyCode("BTC");
        dto.setUserId(1);
        dto.setAmountInUsd(BigDecimal.valueOf(100));

        PurchaseResponseDto response = purchaseService.executePurchase(dto);
        assertEquals("SUCCESS", response.getStatus());
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(logRepository).save(any(AppLog.class));
        verify(purchaseRepository).save(any(Purchase.class));
    }
}
