package com.aim.advice.service;

import com.aim.advice.domain.auth.AuthHistory;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.dto.auth.TokenReissueResponse;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthHistoryRepository authHistoryRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
            );

            String userId = authentication.getName();
            String role = extractRole(authentication);

            String accessToken = jwtUtil.generateToken(authentication.getName(), role);
            String refreshToken = jwtUtil.generateRefreshToken(userId);
            Duration ttl = jwtUtil.getRemainingDuration(refreshToken);

            redisTemplate.opsForValue().set("RT:" + userId, refreshToken, ttl);
            authHistoryRepository.save(AuthHistory.of(authentication.getName(), "LOGIN"));
            return LoginResponse.of(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    @Transactional
    public void logout(String userId, String token) {
        Duration ttl = jwtUtil.getRemainingDuration(token);
        redisTemplate.opsForValue().set("BL:" + token, userId, ttl);
        redisTemplate.delete("RT:" + userId);
        authHistoryRepository.save(AuthHistory.of(userId, "LOGOUT"));
    }

    @Transactional
    public TokenReissueResponse reissue(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        String saved = redisTemplate.opsForValue().get("RT:" + userId);

        if (saved == null || !saved.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        String role = jwtUtil.extractRole(refreshToken);
        String newAccess = jwtUtil.generateToken(userId, role);
        String newRefresh = jwtUtil.generateRefreshToken(userId);

        Duration ttl = jwtUtil.getRemainingDuration(newRefresh);
        redisTemplate.opsForValue().set("RT:" + userId, newRefresh, ttl);

        return TokenReissueResponse.of(newAccess, newRefresh);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return authority.replace("ROLE_", "");
                })
                .orElse("USER");
    }

}
