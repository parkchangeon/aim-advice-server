package com.aim.advice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Key signingKey;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder()
                .encodeToString("0123456789abcdef0123456789abcdef".getBytes());
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "base64Secret", secret);
        jwtUtil.init();

        signingKey = (Key) ReflectionTestUtils.getField(jwtUtil, "signingKey");
    }

    @Test
    @DisplayName("토큰 생성 후 검증, 사용자ID,역할 추출이 정상 동작한다.")
    void generateAndParseToken() {
        // when
        String token = jwtUtil.generateToken("user1", "ROLE_USER");

        // then
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUserId(token)).isEqualTo("user1");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("만료된 토큰은 validateToken()이 false를 반환한다.")
    void expiredTokenReturnsFalse() {
        // given
        String expired = Jwts.builder()
                .setSubject("user1")
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date(System.currentTimeMillis() - Duration.ofHours(25).toMillis()))
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        // when // then
        assertThat(jwtUtil.validateToken(expired)).isFalse();
    }

    @Test
    @DisplayName("getRemainingDuration()는 양수의 남은 기간을 반환한다.")
    void remainingDurationIsPositive() {
        // when
        String token = jwtUtil.generateToken("user1", "ROLE_USER");
        Duration remaining = jwtUtil.getRemainingDuration(token);

        // then
        assertThat(remaining).isPositive();
        assertThat(remaining).isLessThanOrEqualTo(Duration.ofHours(24));
    }
}
