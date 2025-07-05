package com.example.minet.service;

import com.example.minet.dto.*;
import com.example.minet.entities.*;
import com.example.minet.entities.Currency;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.exceptions.InvalidRequestException;
import com.example.minet.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;
    private final WatchlistRepository watchlistRepository;

    public DashboardResponseDto getDashboardForUser(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new InvalidRequestException("User ID must be valid and non-null");
        }

        DashboardResponseDto dto = new DashboardResponseDto();
        dto.setWatchlist(getUserWatchlist(userId));
        dto.setWallets(getUserWallets(userId));
        dto.setPortfolio(getUserPortfolio(userId));
        dto.setRecentTransactions(getUserRecentTransactions(userId));

        return dto;
    }

    private List<WatchlistItemDto> getUserWatchlist(Integer userId) {
        List<Watchlist> watchlist = watchlistRepository.findByUser_UserId(userId);

        if (watchlist.isEmpty()) {
            throw new DataNotFoundException("No watchlist found for user ID: " + userId);
        }

        return watchlist.stream()
                .filter(watch -> watch.getCurrency() != null)
                .map(watch -> {
                    Currency currency = watch.getCurrency();
                    WatchlistItemDto item = new WatchlistItemDto();
                    item.setCurrencyId(currency.getCurrencyId());
                    item.setCurrencyName(currency.getCurrencyName());
                    item.setCurrencyCode(currency.getCurrencyCode());
                    item.setCurrentPrice(Optional.ofNullable(currency.getPrice()).orElse(BigDecimal.ZERO));
                    item.setPriceChange24h(Optional.ofNullable(currency.getChangePercent()).orElse(0.0));
                    return item;
                })
                .toList();
    }

    private List<DashboardResponseDto.WalletInfo> getUserWallets(Integer userId) {
        List<Wallet> wallets = walletRepository.findByUser_UserId(userId);

        if (wallets.isEmpty()) {
            throw new DataNotFoundException("No wallets found for user ID: " + userId);
        }

        return wallets.stream()
                .map(wallet -> {
                    DashboardResponseDto.WalletInfo walletInfo = new DashboardResponseDto.WalletInfo();
                    walletInfo.setCurrency(wallet.getCurrencyName());
                    walletInfo.setBalance(wallet.getBalance());
                    return walletInfo;
                })
                .toList();
    }

    private DashboardResponseDto.Portfolio getUserPortfolio(Integer userId) {
        List<Wallet> wallets = walletRepository.findByUser_UserId(userId);

        if (wallets.isEmpty()) {
            throw new DataNotFoundException("No wallet data found for portfolio calculation.");
        }

        BigDecimal totalBalance = BigDecimal.ZERO;
        List<DashboardResponseDto.CurrencyHolding> holdings = new ArrayList<>();

        for (Wallet wallet : wallets) {
            Currency currency = wallet.getCurrency();
            if (currency == null || currency.getPrice() == null) {
                continue;
            }
            BigDecimal usdValue = currency.getPrice().multiply(wallet.getBalance());
            totalBalance = totalBalance.add(usdValue);

            DashboardResponseDto.CurrencyHolding holding = new DashboardResponseDto.CurrencyHolding();
            holding.setCurrencyName(currency.getCurrencyName());
            holding.setAmountHeld(wallet.getBalance());
            holding.setUsdValue(usdValue);
            holdings.add(holding);
        }

        DashboardResponseDto.Portfolio portfolio = new DashboardResponseDto.Portfolio();
        portfolio.setTotalBalance(totalBalance);
        portfolio.setCurrencies(holdings);
        return portfolio;
    }

    private List<DashboardResponseDto.RecentTransaction> getUserRecentTransactions(Integer userId) {
        List<Trade> trades = tradeRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId);

        if (trades.isEmpty()) {
            return Collections.emptyList(); // Not an exception, just return empty
        }

        return trades.stream()
                .sorted(Comparator.comparing(Trade::getCreatedAt).reversed())
                .limit(5)
                .map(trade -> {
                    DashboardResponseDto.RecentTransaction tx = new DashboardResponseDto.RecentTransaction();
                    tx.setTransactionId(trade.getTradeId());
                    tx.setCurrency(trade.getCurrencyName());
                    tx.setAmount(trade.getAmount());
                    tx.setValueUsd(trade.getAmount().multiply(Optional.ofNullable(trade.getPrice()).orElse(BigDecimal.ZERO)));
                    tx.setTimestamp(trade.getCreatedAt());
                    tx.setType(userId.equals(trade.getBuyer().getUserId()) ? "BUY" : "SELL");
                    return tx;
                })
                .toList();
    }
}