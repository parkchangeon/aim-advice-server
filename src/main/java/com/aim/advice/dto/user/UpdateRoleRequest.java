package com.aim.advice.dto.user;

import com.aim.advice.domain.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleRequest {

    @NotBlank(message = "UserId is required")
    private String userId;

    @NotNull(message = "Role is required")
    private Role role;

    @Builder
    private UpdateRoleRequest(String userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    public static UpdateRoleRequest of(String userId, Role role) {
        return UpdateRoleRequest.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
