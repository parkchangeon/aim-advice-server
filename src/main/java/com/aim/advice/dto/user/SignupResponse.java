package com.aim.advice.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupResponse {
    private Long no;
    private String userId;

    @Builder
    private SignupResponse(Long no, String userId) {
        this.no = no;
        this.userId = userId;
    }

    public static SignupResponse of(Long no, String userId) {
        return SignupResponse.builder()
                .no(no)
                .userId(userId)
                .build();
    }
}
