package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.balance.BalanceRequest;
import com.aim.advice.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/deposit")
    public ApiResponse<BigDecimal> deposit(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BalanceRequest balanceRequest
    ) {
        BigDecimal newBalance = balanceService.deposit(userId, balanceRequest.getAmount());
        return ApiResponse.ok(newBalance);
    }

    @PostMapping("/withdraw")
    public ApiResponse<BigDecimal> withdraw(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BalanceRequest balanceRequest
    ) {
        BigDecimal newBalance = balanceService.withdraw(userId, balanceRequest.getAmount());
        return ApiResponse.ok(newBalance);
    }

}