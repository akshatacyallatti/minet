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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock private UserRepository userRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_NewUser_UsdCurrencyExists() {
        UserDto dto = new UserDto();
        dto.setEmail("test@example.com");
        dto.setName("Test User");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");

        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPass");
        savedUser.setCreatedBy(LocalDateTime.now());

        when(userRepository.save(any())).thenReturn(savedUser);

        Currency usd = new Currency();
        usd.setCurrencyId(1);
        usd.setCurrencyName("USD");
        usd.setCurrencyCode("USD");
        usd.setSymbol("USD");
        usd.setPrice(BigDecimal.ZERO);
        usd.setChangePercent(0.0);

        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.of(usd));

        UserResponseDto result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testRegisterUser_NewUser_UsdCurrencyDoesNotExist() {
        UserDto dto = new UserDto();
        dto.setEmail("test@example.com");
        dto.setName("Test User");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");

        User savedUser = new User();
        savedUser.setUserId(2);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPass");
        savedUser.setCreatedBy(LocalDateTime.now());

        when(userRepository.save(any())).thenReturn(savedUser);
        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.empty());

        Currency newUsd = new Currency();
        newUsd.setCurrencyName("USD");
        newUsd.setCurrencyCode("USD");
        newUsd.setSymbol("USD");
        newUsd.setPrice(BigDecimal.ZERO);
        newUsd.setChangePercent(0.00);

        when(currencyRepository.save(any())).thenReturn(newUsd);

        UserResponseDto result = userService.registerUser(dto);
        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        UserDto dto = new UserDto();
        dto.setEmail("exists@example.com");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AuthenticationFailedException.class, () -> userService.registerUser(dto));
    }

    @Test
    void testSignIn_Success() {
        User user = new User();
        user.setUserId(5);
        user.setName("Test User");
        user.setEmail("login@example.com");
        user.setPassword("encodedPassword");
        user.setCreatedBy(LocalDateTime.now());

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        UserResponseDto result = userService.signIn("login@example.com", "plainPassword");

        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testSignIn_UserNotFound() {
        when(userRepository.findByEmail("absent@example.com")).thenReturn(Optional.empty());
        assertThrows(AuthenticationFailedException.class, () -> userService.signIn("absent@example.com", "any"));
    }

    @Test
    void testSignIn_PasswordMismatch() {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("login@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(AuthenticationFailedException.class, () -> userService.signIn("login@example.com", "wrong"));
    }
}
