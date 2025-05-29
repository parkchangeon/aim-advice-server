package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.TokenReissueRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("로그인 요청 시 JWT Access/Refresh Token을 반환한다.")
    void login() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "password");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("로그인 시 userId가 비어있으면 예외가 발생한다.")
    void loginWithNoUserId() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("", "wrongpassword");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("UserId is required"));
    }

    @Test
    @DisplayName("로그인 시 password가 비어있으면 예외가 발생한다.")
    void loginWithNoPassword() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "");

        // when // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is required"));
    }

    @Test
    @DisplayName("로그아웃 요청을 한다.")
    void logout() throws Exception {
        // given
        RequestPostProcessor userAuth = authentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        // when // then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .with(userAuth)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("로그아웃 요청 시 로그인은 필수이다.")
    void logoutWithNoLogin() throws Exception {
        // when // then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Login is required"));
    }

    @Test
    @DisplayName("토큰 재발급 요청 시 Access/Refresh Token을 반환한다.")
    void reissue() throws Exception {
        // given
        RequestPostProcessor userAuth = authentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
        TokenReissueRequest request = TokenReissueRequest.of("valid.refresh.token");

        // when // then
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .with(csrf())
                        .with(userAuth)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("토큰 재발급 요청 시 Refresh Token 값은 필수이다.")
    void reissueWithNoRefreshToken() throws Exception {
        // given
        RequestPostProcessor userAuth = authentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
        TokenReissueRequest request = TokenReissueRequest.of(null);

        // when // then
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .with(csrf())
                        .with(userAuth)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Refresh token is required"));
    }

}