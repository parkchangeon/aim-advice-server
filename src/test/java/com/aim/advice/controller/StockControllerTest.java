package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.dto.stock.StockRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("관리자가 증권을 등록할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void register() throws Exception {
        // given
        StockRequest request = StockRequest.of("AAPL", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("일반 사용자가 증권을 등록할 수 없다.")
    @WithMockUser(roles = "USER")
    void registerStockWithUser() throws Exception {
        // given
        StockRequest request = StockRequest.of("AAPL", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @DisplayName("admin 계정으로 로그인하지 않으면 증권을 등록할 수 없다.")
    void registerStockWithNoLogin() throws Exception {
        // given
        StockRequest request = StockRequest.of("AAPL", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Login is required"));
    }

    @Test
    @DisplayName("관리자가 증권을 등록할 때 증권코드는 필수이다.")
    @WithMockUser(roles = "ADMIN")
    void registerWithNoCode() throws Exception {
        // given
        StockRequest request = StockRequest.of("", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock code is required"));
    }

    @Test
    @DisplayName("관리자가 증권을 등록할 때 증권이름은 필수이다.")
    @WithMockUser(roles = "ADMIN")
    void registerWithNoName() throws Exception {
        // given
        StockRequest request = StockRequest.of("APPL", "", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock name is required"));
    }

    @Test
    @DisplayName("관리자가 증권을 등록할 때 증권가격은 필수이다.")
    @WithMockUser(roles = "ADMIN")
    void registerWithNoPrice() throws Exception {
        // given
        StockRequest request = StockRequest.of("APPL", "Apple", null);

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock price is required"));
    }

    @Test
    @DisplayName("관리자가 증권을 등록할 때 증권가격은 1원 이상이어야 한다.")
    @WithMockUser(roles = "ADMIN")
    void registerWithPriceMoreThan1() throws Exception {
        // given
        StockRequest request = StockRequest.of("APPL", "Apple", new BigDecimal("0.00"));

        // when // then
        mockMvc.perform(post("/api/v1/stocks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock price must be greater than or equal to 1"));
    }

    @Test
    @DisplayName("관리자가 가격을 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void updatePrice() throws Exception {
        // given
        StockRequest request = StockRequest.of("AAPL", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(put("/api/v1/stocks/price")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("관리자가 증권 가격을 수정할 때 증권코드는 필수이다.")
    @WithMockUser(roles = "ADMIN")
    void updatePriceWithNoCode() throws Exception {
        // given
        StockRequest request = StockRequest.of("", "Apple", new BigDecimal("150.00"));

        // when // then
        mockMvc.perform(put("/api/v1/stocks/price")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock code is required"));
    }

    @Test
    @DisplayName("관리자가 증권 가격을 수정할 때 증권가격은 필수이다.")
    @WithMockUser(roles = "ADMIN")
    void updatePriceWithNoPrice() throws Exception {
        // given
        StockRequest request = StockRequest.of("APPL", "Apple", null);

        // when // then
        mockMvc.perform(put("/api/v1/stocks/price")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock price is required"));
    }

    @Test
    @DisplayName("관리자가 증권 가격을 수정할 때 증권가격은 1원 이상이어야 한다.")
    @WithMockUser(roles = "ADMIN")
    void updatePriceWithPriceMoreThan1() throws Exception {
        // given
        StockRequest request = StockRequest.of("APPL", "Apple", new BigDecimal("0.00"));

        // when // then
        mockMvc.perform(put("/api/v1/stocks/price")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Stock price must be greater than or equal to 1"));
    }

}