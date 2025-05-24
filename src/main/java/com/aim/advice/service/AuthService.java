package com.aim.advice.service;

import com.aim.advice.domain.auth.AuthHistory;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
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
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        return authority.replace("ROLE_", "");
                    })
                    .orElse("USER");
            String token = jwtUtil.generateToken(authentication.getName(), role);
            authHistoryRepository.save(AuthHistory.of(authentication.getName(), "LOGIN"));
            return LoginResponse.of(token);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    @Transactional
    public void logout(String userId, String token) {
        Duration ttl = jwtUtil.getRemainingDuration(token);
        redisTemplate.opsForValue().set("BL:" + token, userId, ttl);
        authHistoryRepository.save(AuthHistory.of(userId, "LOGOUT"));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
