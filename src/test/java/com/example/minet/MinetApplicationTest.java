package com.example.minet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
@SpringBootTest
class MinetApplicationTest {
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> MinetApplication.main(new String[]{}));
    }
}

