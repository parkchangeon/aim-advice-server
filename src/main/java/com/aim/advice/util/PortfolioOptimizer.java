package com.aim.advice.util;

import com.aim.advice.domain.stock.Stock;
import com.aim.advice.dto.advice.InvestedStock;
import com.aim.advice.dto.advice.PortfolioResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PortfolioOptimizer {
    private static final int SCALE = 2;

    public static PortfolioResult optimize(BigDecimal budget, List<Stock> stocks) {
        List<Stock> sorted = stocks.stream()
                .sorted(Comparator.comparing(Stock::getPrice))
                .toList();

        List<InvestedStock> result = new ArrayList<>();
        BigDecimal usedAmount = BigDecimal.ZERO;

        for (Stock stock : sorted) {
            if (stock.getPrice().compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal[] divmod = budget.divideAndRemainder(stock.getPrice());
            int quantity = divmod[0].intValue(); // 최대 매수 수량

            if (quantity > 0) {
                BigDecimal spent = stock.getPrice().multiply(BigDecimal.valueOf(quantity));
                usedAmount = usedAmount.add(spent);
                budget = budget.subtract(spent);

                result.add(InvestedStock.of(
                        stock.getCode(),
                        stock.getName(),
                        quantity,
                        stock.getPrice()
                ));
            }

            if (budget.compareTo(BigDecimal.ZERO) <= 0) break;
        }

        if (result.isEmpty()) {
            throw new IllegalStateException("No stocks can be bought with the given budget");
        }

        return PortfolioResult.of(result, usedAmount);
    }

}
