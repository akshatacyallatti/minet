package com.example.minet.controllers;

import com.example.minet.constants.OrderStatus;
import com.example.minet.constants.OrderType;
import com.example.minet.dto.OrderRequestDto;
import com.example.minet.dto.OrderResponseDto;
import com.example.minet.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOrdersByUser_Success() throws Exception {
        int userId = 1;

        OrderResponseDto mockOrder = new OrderResponseDto();
        mockOrder.setOrderId(1001);
        mockOrder.setCurrencySymbol("BTC");
        mockOrder.setOrderType(OrderType.BUY);
        mockOrder.setBalance(BigDecimal.valueOf(0.5));
        mockOrder.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderService.getOrdersByUserDto(eq(userId)))
                .thenReturn(List.of(mockOrder));

        mockMvc.perform(get("/api/v1/orders/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1001))
                .andExpect(jsonPath("$[0].currency").value("BTC"))
                .andExpect(jsonPath("$[0].orderType").value("BUY"))
                .andExpect(jsonPath("$[0].amount").value(0.5))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void testPlaceOrder_Success() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setUserId(1);
        requestDto.setCurrencyId(2);
        requestDto.setAmount(1.0);
        requestDto.setOrderType("SELL");

        Mockito.doNothing().when(orderService).placeOrder(any(OrderRequestDto.class));

        mockMvc.perform(post("/api/v1/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully."));
    }
}
