package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.dto.auth.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("로그인 요청 시 JWT 토큰을 반환한다.")
    @Test
    void login() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "password");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("로그인 시 userId가 비어있으면 예외가 발생한다.")
    @Test
    void loginWithNoUserId() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("", "wrongpassword");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("UserId is required"));
    }

    @DisplayName("로그인 시 password가 비어있으면 예외가 발생한다.")
    @Test
    void loginWithNoPassword() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is required"));
    }

    @DisplayName("로그아웃 요청 시 200 OK를 반환한다.")
    @Test
    void logout() throws Exception {
        // given
        RequestPostProcessor userAuth = authentication(
                new UsernamePasswordAuthenticationToken("user1", null)
        );

        // when // then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .with(userAuth)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(authService).logout("user1", "token");
    }

}