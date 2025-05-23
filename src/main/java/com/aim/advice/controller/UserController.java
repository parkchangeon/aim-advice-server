package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.user.SignupRequest;
import com.aim.advice.dto.user.UpdateRoleRequest;
import com.aim.advice.service.UserService;
import com.aim.advice.dto.user.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(userService.signup(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ApiResponse<Void> updateRole(@Valid @RequestBody UpdateRoleRequest request) {
        userService.updateRole(request.getUserId(), request.getRole());
        return ApiResponse.ok(null);
    }

}