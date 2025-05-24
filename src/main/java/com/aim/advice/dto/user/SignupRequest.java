package com.aim.advice.dto.user;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@GroupSequence({SignupRequest.NotBlankGroup.class, SignupRequest.class})
public class SignupRequest {

    @NotBlank(message = "UserId is required", groups = NotBlankGroup.class)
    @Size(min = 4, max = 16, message = "userId size must be between 4 and 16")
    private String userId;
    @NotBlank(message = "Password is required", groups = NotBlankGroup.class)
    @Size(min = 8, max = 16, message = "password size must be between 8 and 16")
    private String password;

    @Builder
    private SignupRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static SignupRequest of(String userId, String password) {
        return SignupRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public interface NotBlankGroup {
    }
}
