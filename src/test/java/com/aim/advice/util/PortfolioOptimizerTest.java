package com.aim.advice.util;

import com.aim.advice.domain.stock.Stock;
import com.aim.advice.dto.advice.InvestedStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortfolioOptimizerTest {

    @Test
    @DisplayName("주어진 금액으로 증권 포트폴리오를 구성한다.")
    void optimize() {
        // given
        List<Stock> stocks = List.of(
                Stock.of(1L, "stockA", "A", new BigDecimal("30000")),
                Stock.of(2L, "stockB", "B", new BigDecimal("20000")),
                Stock.of(3L, "stockC", "C", new BigDecimal("50000"))
        );
        BigDecimal budget = new BigDecimal("100000");

        // when
        List<InvestedStock> result = PortfolioOptimizer.optimize(budget, stocks);

        // then
        int totalInvested = result.stream()
                .mapToInt(s -> s.getPrice().intValue() * s.getQuantity())
                .sum();
        assertThat(totalInvested).isLessThanOrEqualTo(budget.intValue());
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("주어진 금액으로 증권 포트폴리오를 구성할 수 없는 경우 예외가 발생한다")
    void optimizeWithBalanceLessThanStockPrice() {
        // given
        List<Stock> stocks = List.of(
                Stock.of(1L, "stockA", "A", new BigDecimal("30000")),
                Stock.of(2L, "stockB", "B", new BigDecimal("20000"))
        );
        BigDecimal budget = new BigDecimal("10000");

        // when // then
        assertThatThrownBy(() -> PortfolioOptimizer.optimize(budget, stocks))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No stocks can be bought with the given budget");
    }

    @Test
    @DisplayName("증권 가격이 소수점일 때도 증권 포트폴리오를 구성한다.")
    void optimizeWithDecimalStockPrice() {
        // given
        List<Stock> stocks = List.of(
                Stock.of(1L, "stockA", "A", new BigDecimal("3333.33")),
                Stock.of(2L, "stockB", "B", new BigDecimal("6666.66"))
        );
        BigDecimal budget = new BigDecimal("10000");

        // when
        List<InvestedStock> result = PortfolioOptimizer.optimize(budget, stocks);

        // then
        int totalInvested = result.stream()
                .mapToInt(s -> s.getPrice().intValue() * s.getQuantity())
                .sum();
        assertThat(totalInvested).isLessThanOrEqualTo(budget.intValue());
        assertThat(result).isNotEmpty();
    }
}
