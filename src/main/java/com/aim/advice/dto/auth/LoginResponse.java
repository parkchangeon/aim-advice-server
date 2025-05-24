package com.aim.advice.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String token;

    @Builder
    private LoginResponse(String token) {
        this.token = token;
    }

    public static LoginResponse of(String token) {
        return LoginResponse.builder()
                .token(token)
                .build();
    }
}
