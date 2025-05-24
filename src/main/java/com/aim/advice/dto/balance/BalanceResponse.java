package com.aim.advice.dto.balance;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BalanceResponse {
    private final BigDecimal balance;

    @Builder
    private BalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public static BalanceResponse of(BigDecimal balance) {
        return BalanceResponse.builder()
                .balance(balance)
                .build();
    }
}
