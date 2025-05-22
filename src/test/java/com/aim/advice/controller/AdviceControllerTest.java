package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.domain.advice.RiskType;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdviceControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("위험도를 선택하고 자문을 요청하면 투자금액과 잔여금액을 응답한다.")
    @WithMockUser(username = "user1")
    void requestAdvice() throws Exception {
        // given
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);
        AdviceResponse response = AdviceResponse.of(new BigDecimal("100000.00"), new BigDecimal("0.00"));
        when(adviceService.requestAdvice(any(), any())).thenReturn(response);

        // when // then
        mockMvc.perform(
                        post("/api/v1/advice")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.investedAmount").value(100000.00))
                .andExpect(jsonPath("$.data.remainingBalance").value(0.00));
    }

    @Test
    @DisplayName("로그인하지 않으면 자문 요청이 실패한다.")
    void requestAdviceWithoutLogin() throws Exception {
        // given
        AdviceRequest request = AdviceRequest.of(RiskType.MEDIUM);

        // when // then
        mockMvc.perform(post("/api/v1/advice")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Login is required"));
    }

    @Test
    @DisplayName("자문 요청 시 요청 본문은 필수이다.")
    @WithMockUser(username = "user1")
    void requestAdviceWithEmptyBody() throws Exception {
        // when // then
        mockMvc.perform(post("/api/v1/advice")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

    @Test
    @DisplayName("자문 요청 시 위험도 유형은 필수이다.")
    @WithMockUser(username = "user1")
    void requestAdviceWithNoRiskType() throws Exception {
        // given
        AdviceRequest request = AdviceRequest.of(null);

        // when // then
        mockMvc.perform(post("/api/v1/advice")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("RiskType is required"));
    }

    @Test
    @DisplayName("올바른 위험도 유형으로 자문 요청을 해야 한다.")
    @WithMockUser(username = "user1")
    void requestAdviceWithRightRiskType() throws Exception {
        // given
        String invalidJson = """
                    {
                      "riskType": "INVALID"
                    }
                """;

        // when // then
        mockMvc.perform(post("/api/v1/advice")
                        .with(csrf())
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

}