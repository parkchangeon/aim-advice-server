package com.aim.advice.util;

import com.aim.advice.domain.stock.Stock;
import com.aim.advice.dto.advice.InvestedStock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PortfolioOptimizer {
    private static final int SCALE = 2;

    public static List<InvestedStock> optimize(BigDecimal budget, List<Stock> stocks) {
        int scaledBudget = budget.multiply(BigDecimal.TEN.pow(SCALE)).intValueExact();
        int[] dp = new int[scaledBudget + 1];
        int[] lastUsed = new int[scaledBudget + 1];
        Arrays.fill(lastUsed, -1);

        List<Integer> scaledPrices = stocks.stream()
                .map(stock -> stock.getPrice().multiply(BigDecimal.TEN.pow(SCALE)).intValueExact())
                .toList();

        boolean hasBuyableStock = scaledPrices.stream().anyMatch(price -> price <= scaledBudget);
        if (!hasBuyableStock) {
            throw new IllegalStateException("No stocks can be bought with the given budget");
        }

        for (int i = 0; i < stocks.size(); i++) {
            int price = scaledPrices.get(i);
            for (int j = price; j <= scaledBudget; j++) {
                if (dp[j - price] + price > dp[j]) {
                    dp[j] = dp[j - price] + price;
                    lastUsed[j] = i;
                }
            }
        }

        Map<Long, Integer> result = new LinkedHashMap<>();
        int remaining = Arrays.stream(dp).max().orElse(0);
        while (remaining > 0 && lastUsed[remaining] != -1) {
            int idx = lastUsed[remaining];
            Stock stock = stocks.get(idx);
            result.put(stock.getNo(), result.getOrDefault(stock.getNo(), 0) + 1);
            remaining -= scaledPrices.get(idx);
        }

        return getInvestedStocks(stocks, result);
    }

    private static List<InvestedStock> getInvestedStocks(List<Stock> stocks, Map<Long, Integer> result) {
        Map<Long, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getNo, Function.identity()));

        return result.entrySet().stream()
                .map(entry -> {
                    Stock stock = stockMap.get(entry.getKey());
                    return InvestedStock.of(stock.getCode(), stock.getName(), entry.getValue(), stock.getPrice());
                })
                .toList();
    }
}
