package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.dto.balance.BalanceRequest;
import com.aim.advice.dto.balance.BalanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("금액을 받아 입금한다.")
    @WithMockUser(username = "user1")
    void deposit() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(new BigDecimal("150.00"));
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/deposit")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("입금할 때 로그인은 필수이다.")
    void depositWithLogin() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(null);
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/deposit")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Login is required"));
    }

    @Test
    @DisplayName("입금할 때 request body가 아예 없는 경우 입금할 수 없다.")
    @WithMockUser(username = "user1")
    void depositWithRequestBody() throws Exception {
        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/deposit")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

    @Test
    @DisplayName("금액을 받아 입금할 때 금액은 필수이다.")
    @WithMockUser(username = "user1")
    void depositWithAmount() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(null);
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/deposit")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Amount cannot be null"));
    }

    @Test
    @DisplayName("금액을 받아 입금할 때 금액은 0보다 커야한다.")
    @WithMockUser(username = "user1")
    void depositWithAmountMoreThan0() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(new BigDecimal("0"));
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/deposit")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Amount must be greater than or equal to 1"));
    }

    @Test
    @DisplayName("금액을 받아 출금한다.")
    @WithMockUser(username = "user1")
    void withdraw() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/balance/withdraw")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("출금할 때 로그인은 필수이다.")
    void withdrawWithLogin() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/withdraw")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Login is required"));
    }

    @Test
    @DisplayName("출금할 때 request body가 아예 없는 경우 출금할 수 없다.")
    @WithMockUser(username = "user1")
    void withdrawWithRequestBody() throws Exception {
        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/withdraw")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

    @Test
    @DisplayName("금액을 받아 출금할 때 금액은 필수이다.")
    @WithMockUser(username = "user1")
    void withdrawWithAmount() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(null);
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/withdraw")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Amount cannot be null"));
    }

    @Test
    @DisplayName("금액을 받아 출금할 때 금액은 0보다 커야한다.")
    @WithMockUser(username = "user1")
    void withdrawWithAmountMoreThan0() throws Exception {
        // given
        BalanceRequest request = BalanceRequest.of(new BigDecimal("0"));
        when(balanceService.deposit(anyString(), any(BigDecimal.class)))
                .thenReturn(BalanceResponse.of(request.getAmount()));

        // when // then
        mockMvc.perform(
                        post("/api/v1/balance/withdraw")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Amount must be greater than or equal to 1"));
    }

    @DisplayName("고객의 잔액을 조회한다.")
    @Test
    void getAvailableAdvertisements() throws Exception {
        //given
        RequestPostProcessor userAuth = authentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
        BalanceResponse result = BalanceResponse.of(new BigDecimal("100.00"));
        when(balanceService.inquireBalance(anyString())).thenReturn(result);

        //when //then
        mockMvc.perform(
                        get("/api/v1/balance")
                                .with(csrf())
                                .with(userAuth)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.balance").value(100.00));
    }
}