package com.example.minet.service;

import com.example.minet.dto.WatchlistRequestDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Watchlist;
import com.example.minet.exceptions.DataNotFoundException;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.UserRepository;
import com.example.minet.repositories.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    public String toggleWatchlist(WatchlistRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + dto.getUserId()));

        Currency currency = currencyRepository.findById(dto.getCurrencyId())
                .orElseThrow(() -> new DataNotFoundException("Currency not found with ID: " + dto.getCurrencyId()));

        return watchlistRepository.findByUserAndCurrency(user, currency)
                .map(existing -> {
                    watchlistRepository.delete(existing);
                    return "Currency removed from watchlist";
                })
                .orElseGet(() -> {
                    Watchlist watchlist = new Watchlist();
                    watchlist.setUser(user);
                    watchlist.setCurrency(currency);
                    watchlist.setAddedAt(LocalDateTime.now());
                    watchlistRepository.save(watchlist);
                    return "Currency added to watchlist";
                });
    }

    public List<Watchlist> getWatchlistByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + userId));

        return watchlistRepository.findAllByUser(user);
    }
}
