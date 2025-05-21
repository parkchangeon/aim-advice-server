package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aim.advice.domain.AuthHistory;
import com.aim.advice.domain.User;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.repository.UserRepository;
import com.aim.advice.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class AuthServiceTest extends IntegrationTestSupport{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthHistoryRepository authHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;


    @DisplayName("유효한 사용자 정보로 로그인 시 JWT 토큰을 반환하고 히스토리를 저장한다.")
    @Test
    void login() {
        // given
        String rawPassword = "password123";
        User user = User.of("user1", passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        // when
        LoginResponse loginResponse = authService.login(LoginRequest.of("user1", rawPassword));

        // then
        assertThat(loginResponse.getToken()).isNotBlank();

        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(user.getUserId(), "LOGIN");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(user.getUserId(), "LOGIN")
                );
    }

    @DisplayName("존재하지 않는 사용자로 로그인 시 예외가 발생한다.")
    @Test
    void loginWithUserNotFound() {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "password");

        // when // then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @DisplayName("비밀번호 불일치 시 예외가 발생한다.")
    @Test
    void loginWithBadPassword() {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "worngpassword");
        User user = User.of("user1", "password");
        userRepository.save(user);

        // when // then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @DisplayName("유효한 토큰으로 로그아웃 시 히스토리를 저장한다.")
    @Test
    void logout() {
        // given
        String rawPassword = "password123";
        User user = User.of("user1", passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        authService.login(LoginRequest.of("user1", rawPassword));

        // when
        authService.logout(user.getUserId());

        // then
        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(user.getUserId(), "LOGOUT");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(user.getUserId(), "LOGOUT")
                );
    }

    @DisplayName("유효하지 않은 토큰으로 로그아웃 시 히스토리를 저장하지 않는다.")
    @Test
    void logoutWithInvalidToken() {
        // given
        String token = "badToken";

        // when
        authService.logout(token);

        // then
        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction("user1", "LOGOUT");
        assertThat(authHistories).isEmpty();
    }
}