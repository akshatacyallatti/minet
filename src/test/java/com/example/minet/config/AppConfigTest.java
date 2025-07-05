package com.example.minet.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testPasswordEncoderBean() {
        AppConfig config = new AppConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder, "PasswordEncoder bean should not be null");

        String rawPassword = "mySecret123";
        String encodedPassword = encoder.encode(rawPassword);

        assertTrue(encoder.matches(rawPassword, encodedPassword),
                "Encoded password should match the raw password");

        assertNotEquals(rawPassword, encodedPassword,
                "Encoded password should not be the same as raw password");
    }
}
