package com.aim.advice.dto.advice;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class PortfolioResult {
    private final List<InvestedStock> investedStocks;
    private final BigDecimal usedAmount;

    @Builder
    private PortfolioResult(List<InvestedStock> investedStocks, BigDecimal usedAmount) {
        this.investedStocks = investedStocks;
        this.usedAmount = usedAmount;
    }

    public static PortfolioResult of(List<InvestedStock> investedStocks, BigDecimal usedAmount) {
        return PortfolioResult.builder()
                .investedStocks(investedStocks)
                .usedAmount(usedAmount)
                .build();
    }
}
