package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.auth.LoginRequest;
import com.aim.advice.dto.auth.LoginResponse;
import com.aim.advice.dto.auth.TokenReissueRequest;
import com.aim.advice.dto.auth.TokenReissueResponse;
import com.aim.advice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ApiResponse.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal String userId, HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(userId, token);
        return ApiResponse.ok(null);
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenReissueResponse> reissue(@Valid @RequestBody TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        return ApiResponse.ok(authService.reissue(refreshToken));
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}