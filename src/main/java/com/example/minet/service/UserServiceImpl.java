package com.example.minet.service;

import com.example.minet.dto.UserDto;
import com.example.minet.dto.UserResponseDto;
import com.example.minet.entities.Currency;
import com.example.minet.entities.User;
import com.example.minet.entities.Wallet;
import com.example.minet.exceptions.AuthenticationFailedException;
import com.example.minet.repositories.CurrencyRepository;
import com.example.minet.repositories.UserRepository;
import com.example.minet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CurrencyRepository currencyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto registerUser(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new AuthenticationFailedException("User already registered. Please log in.");
        });

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);
        user.setCreatedBy(LocalDateTime.now());
        User createdUser = userRepository.save(user);

        Currency usd = currencyRepository.findBySymbol("USD").orElseGet(() -> {
            Currency currency = new Currency();
            currency.setCurrencyName("USD");
            currency.setSymbol("USD");
            currency.setCurrencyCode("USD");
            currency.setPrice(BigDecimal.ZERO);
            currency.setChangePercent(0.00);
            return currencyRepository.save(currency);
        });

        Wallet wallet = new Wallet();
        wallet.setUser(createdUser);
        wallet.setCurrency(usd);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrencyName(usd.getCurrencyName());
        wallet.setCurrencyCode("USD");
        walletRepository.save(wallet);

        return mapToDto(createdUser);
    }
    @Override
    public UserResponseDto signIn(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthenticationFailedException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        return mapToDto(user);
    }
    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedBy());
        return dto;
    }
}
