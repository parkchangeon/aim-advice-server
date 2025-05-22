package com.aim.advice.dto.balance;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BalanceResponse {
    private BigDecimal amount;

    @Builder
    private BalanceResponse(BigDecimal amount) {
        this.amount = amount;
    }

    public static BalanceResponse of(BigDecimal amount) {
        return BalanceResponse.builder()
                .amount(amount)
                .build();
    }
}
