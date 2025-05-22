package com.aim.advice.service;

import com.aim.advice.domain.AuthHistory;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthHistoryRepository authHistoryRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
            );
            String token = jwtUtil.generateToken(authentication.getName());
            authHistoryRepository.save(AuthHistory.of(authentication.getName(), "LOGIN"));
            return LoginResponse.of(token);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    @Transactional
    public void logout(String userId) {
        authHistoryRepository.save(AuthHistory.of(userId, "LOGOUT"));
    }
}
