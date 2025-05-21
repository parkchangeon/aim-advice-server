package com.aim.advice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.aim.advice.repository.UserRepository;
import com.aim.advice.repository.AuthHistoryRepository;
import com.aim.advice.security.JwtUtil;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.domain.User;
import com.aim.advice.domain.AuthHistory;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthHistoryRepository authHistoryRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!pwdEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUserId());
        authHistoryRepository.save(AuthHistory.of(user.getUserId(), "LOGIN"));
        return LoginResponse.of(token);
    }

    @Transactional
    public void logout(String userId) {
        authHistoryRepository.save(AuthHistory.of(userId, "LOGOUT"));
    }
}
