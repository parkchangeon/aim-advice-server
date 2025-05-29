package com.aim.advice.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenReissueResponse {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    private TokenReissueResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenReissueResponse of(String accessToken, String refreshToken) {
        return TokenReissueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
