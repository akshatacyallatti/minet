package com.example.minet.controllers;

import com.example.minet.dto.DashboardResponseDto;
import com.example.minet.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetDashboard_Success() throws Exception {
        int userId = 101;

        DashboardResponseDto mockResponse = new DashboardResponseDto();

        Mockito.when(dashboardService.getDashboardForUser(userId))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/dashboard/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());    }
}

