package com.aim.advice.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    private LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
