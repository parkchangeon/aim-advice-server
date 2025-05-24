package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.balance.BalanceRequest;
import com.aim.advice.dto.balance.BalanceResponse;
import com.aim.advice.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/deposit")
    public ApiResponse<BalanceResponse> deposit(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BalanceRequest balanceRequest
    ) {
        return ApiResponse.ok(balanceService.deposit(userId, balanceRequest.getAmount()));
    }

    @PostMapping("/withdraw")
    public ApiResponse<BalanceResponse> withdraw(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BalanceRequest balanceRequest
    ) {
        return ApiResponse.ok(balanceService.withdraw(userId, balanceRequest.getAmount()));
    }

    @GetMapping
    public ApiResponse<BalanceResponse> balance(@AuthenticationPrincipal String userId) {
        return ApiResponse.ok(balanceService.inquireBalance(userId));
    }

}