package com.aim.advice.dto.auth;

import com.aim.advice.dto.user.SignupRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "UserId is required")
    private String userId;

    @NotBlank(message = "Password is required")
    private String password;

    @Builder
    private LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static LoginRequest of(String userId, String password) {
        return LoginRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
