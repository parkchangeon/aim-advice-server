package com.aim.advice.dto.advice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AdviceResponse {
    private BigDecimal investedAmount;
    private BigDecimal remainingBalance;

    @Builder
    private AdviceResponse(BigDecimal investedAmount, BigDecimal remainingBalance) {
        this.investedAmount = investedAmount;
        this.remainingBalance = remainingBalance;
    }

    public static AdviceResponse of(BigDecimal investedAmount, BigDecimal remainingBalance) {
        return AdviceResponse.builder()
                .investedAmount(investedAmount)
                .remainingBalance(remainingBalance)
                .build();
    }
}
