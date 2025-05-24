package com.aim.advice.dto.advice;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class AdviceResponse {
    private final BigDecimal investedAmount;
    private final BigDecimal remainingBalance;
    private final List<InvestedStock> investedStocks;

    @Builder
    private AdviceResponse(BigDecimal investedAmount, BigDecimal remainingBalance, List<InvestedStock> investedStocks) {
        this.investedAmount = investedAmount;
        this.remainingBalance = remainingBalance;
        this.investedStocks = investedStocks;
    }

    public static AdviceResponse of(BigDecimal investedAmount, BigDecimal remainingBalance, List<InvestedStock> investedStocks) {
        return AdviceResponse.builder()
                .investedAmount(investedAmount)
                .remainingBalance(remainingBalance)
                .investedStocks(investedStocks)
                .build();
    }
}
