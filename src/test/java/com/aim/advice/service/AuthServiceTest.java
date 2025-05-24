package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.auth.AuthHistory;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.repository.UserRepository;
import com.aim.advice.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthHistoryRepository authHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @MockitoBean
    private ValueOperations<String, String> valueOperations;



    @DisplayName("유효한 사용자 정보로 로그인 시 JWT 토큰을 반환하고 히스토리를 저장한다.")
    @Test
    void login() {
        // given
        String rawPassword = "password123";
        String userId = "user1";

        User user = User.of(userId, passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        when(jwtUtil.generateToken(eq(userId), any())).thenReturn("fake.jwt.token");

        // when
        LoginResponse loginResponse = authService.login(LoginRequest.of(userId, rawPassword));

        // then
        assertThat(loginResponse.getToken()).isNotBlank();

        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(userId, "LOGIN");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(userId, "LOGIN")
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

    @DisplayName("유효한 토큰으로 로그아웃 시 블랙리스트와 히스토리를 저장한다.")
    @Test
    void logout() {
        // given
        String rawPassword = "password123";
        Duration ttl = Duration.ofSeconds(3600);

        User user = User.of("user1", passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        LoginResponse loginResponse = authService.login(LoginRequest.of("user1", rawPassword));
        String token = loginResponse.getToken();

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getRemainingDuration(token)).thenReturn(ttl);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        authService.logout(user.getUserId(), token);

        // then
        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(user.getUserId(), "LOGOUT");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(user.getUserId(), "LOGOUT")
                );

        verify(redisTemplate.opsForValue()).set("BL:" + token, user.getUserId(), ttl);
    }

}