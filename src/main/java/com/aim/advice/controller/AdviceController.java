package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import com.aim.advice.service.AdviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/advice")
@RequiredArgsConstructor
public class AdviceController {

    private final AdviceService adviceService;

    @PostMapping
    public ApiResponse<AdviceResponse> requestAdvice(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody AdviceRequest request
    ) {
        AdviceResponse response = adviceService.requestAdvice(userId, request);
        return ApiResponse.ok(response);
    }
}

