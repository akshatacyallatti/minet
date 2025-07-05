package com.example.minet.controllers;

import com.example.minet.dto.UserDto;
import com.example.minet.dto.UserResponseDto;
import com.example.minet.service.UserService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService = Mockito.mock(UserService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser_Success() throws Exception {
        UserDto request = new UserDto();
        request.setName("Akshata");
        request.setEmail("akshata@example.com");
        request.setPassword("mySecret123");

        UserResponseDto response = new UserResponseDto();
        response.setName("Akshata");
        response.setEmail("akshata@example.com");

        when(userService.registerUser(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Akshata")))
                .andExpect(jsonPath("$.email", is("akshata@example.com")));
    }

    @Test
    void testLogin_Success() throws Exception {
        UserDto request = new UserDto();
        request.setEmail("akshata@example.com");
        request.setPassword("password");

        UserResponseDto response = new UserResponseDto();
        response.setName("Akshata");
        response.setEmail("akshata@example.com");

        when(userService.signIn(eq("akshata@example.com"), eq("password"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Akshata")))
                .andExpect(jsonPath("$.email", is("akshata@example.com")));
    }
}
