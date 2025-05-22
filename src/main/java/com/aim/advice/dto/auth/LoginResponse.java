package com.aim.advice.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;

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
