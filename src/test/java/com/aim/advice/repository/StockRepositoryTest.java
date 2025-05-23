package com.aim.advice.repository;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StockRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("StockCode로 존재 여부를 확인할 수 있다.")
    void existsByStockCode() {
        // given
        stockRepository.save(Stock.of("AAPL", "Apple", new BigDecimal("150.00")));

        // when
        boolean exists = stockRepository.existsByCode("AAPL");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("StockCode로 Stock을 조회할 수 있다.")
    void findByStockCode() {
        // given
        stockRepository.save(Stock.of("TSLA", "Tesla", new BigDecimal("720.50")));

        // when
        Optional<Stock> result = stockRepository.findByCode("TSLA");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Tesla");
        assertThat(result.get().getPrice()).isEqualByComparingTo("720.50");
    }

    @Test
    @DisplayName("Stock 개수를 확인할 수 있다.")
    void countStocks() {
        // given
        stockRepository.save(Stock.of("KAKAO", "Kakao", new BigDecimal("60.00")));
        stockRepository.save(Stock.of("NAVER", "Naver", new BigDecimal("190.00")));

        // when
        long count = stockRepository.count();

        // then
        assertThat(count).isEqualTo(2);
    }

}