package com.aim.advice.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class TokenReissueRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    @Builder
    private TokenReissueRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static TokenReissueRequest of(String refreshToken) {
        return TokenReissueRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }
}
