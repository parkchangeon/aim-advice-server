package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.auth.AuthHistory;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.dto.auth.TokenReissueResponse;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.repository.UserRepository;
import com.aim.advice.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthHistoryRepository authHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @MockitoBean
    private ValueOperations<String, String> valueOperations;


    @Test
    @DisplayName("유효한 사용자 정보로 로그인 시 JWT Access Token, Refresh Token을 반환하고 히스토리를 저장한다.")
    void login() {
        // given
        String rawPassword = "password123";
        String userId = "user1";

        User user = User.of(userId, passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("RT:" + userId)).thenReturn("fake.jwt.RefreshToken");

        // when
        LoginResponse loginResponse = authService.login(LoginRequest.of(userId, rawPassword));

        // then
        String refreshToken = loginResponse.getRefreshToken();
        assertThat(loginResponse.getAccessToken()).isNotBlank();
        assertThat(refreshToken).isNotBlank();
        assertThat(redisTemplate.opsForValue().get("RT:" + userId)).isNotBlank();
        verify(redisTemplate.opsForValue()).set(eq("RT:" + userId), eq(refreshToken), any(Duration.class));

        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(userId, "LOGIN");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(userId, "LOGIN")
                );
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시 예외가 발생한다.")
    void loginWithUserNotFound() {
        // given
        LoginRequest loginRequest = LoginRequest.of("user1", "password");

        // when // then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외가 발생한다.")
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

    @Test
    @DisplayName("유효한 토큰으로 로그아웃 시 블랙리스트와 히스토리를 저장하고 refresh token을 삭제한다.")
    void logout() {
        // given
        String rawPassword = "password123";
        User user = User.of("user1", passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        LoginResponse loginResponse = authService.login(LoginRequest.of("user1", rawPassword));
        String accessToken = loginResponse.getAccessToken();

        // when
        authService.logout(user.getUserId(), accessToken);

        // then
        List<AuthHistory> authHistories = authHistoryRepository.findByUserIdAndAction(user.getUserId(), "LOGOUT");
        assertThat(authHistories).hasSize(1)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactlyInAnyOrder(
                        tuple(user.getUserId(), "LOGOUT")
                );
        verify(redisTemplate.opsForValue()).set(eq("BL:" + accessToken), eq(user.getUserId()), any(Duration.class));
        verify(redisTemplate).delete("RT:" + user.getUserId());
    }

    @Test
    @DisplayName("유효한 Refresh Token으로 토큰 재발급에 성공하고 Redis에 새로운 Refresh Token을 저장한다.")
    void reissue() {
        // given
        String userId = "user1";
        String password = "password1234";
        String encoded = passwordEncoder.encode(password);
        User user = User.of(userId, encoded);
        userRepository.save(user);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        LoginResponse loginResponse = authService.login(LoginRequest.of(userId, password));
        String refreshToken = loginResponse.getRefreshToken();
        when(valueOperations.get("RT:" + userId)).thenReturn(refreshToken);

        // when
        TokenReissueResponse response = authService.reissue(refreshToken);

        // then
        String newRefreshToken = response.getRefreshToken();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(newRefreshToken).isNotBlank();
        InOrder inOrder = inOrder(valueOperations);
        inOrder.verify(valueOperations).set(eq("RT:" + userId), eq(refreshToken), any(Duration.class)); // login
        inOrder.verify(valueOperations).set(eq("RT:" + userId), eq(newRefreshToken), any(Duration.class)); // reissue
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token일 경우 예외가 발생한다.")
    void reissueWithInvalidToken() {
        // given
        String invalidRefreshToken = "invalid.refresh.token";

        // when // then
        assertThatThrownBy(() -> authService.reissue(invalidRefreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid refresh token");
    }

    @DisplayName("Redis에 저장된 Refresh Token과 다를 경우 예외가 발생한다.")
    @Test
    void reissueWithTokenMismatch() {
        // given
        String userId = "user1";
        String password = "password1234";
        String encoded = passwordEncoder.encode(password);
        User user = User.of(userId, encoded);
        userRepository.save(user);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        LoginResponse loginResponse = authService.login(LoginRequest.of(userId, password));
        String refreshToken = loginResponse.getRefreshToken();
        when(valueOperations.get("RT:" + userId)).thenReturn(refreshToken);

        // when // then
        assertThatThrownBy(() -> authService.reissue(jwtUtil.generateRefreshToken("user2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token mismatch");
    }

}