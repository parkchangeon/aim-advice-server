package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.stock.Stock;
import com.aim.advice.dto.stock.StockRequest;
import com.aim.advice.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockServiceTest extends IntegrationTestSupport {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("증권 정보를 받아 신규 증권을 등록한다.")
    void register() {
        // given
        StockRequest request = StockRequest.of("AAPL", "Apple", new BigDecimal("150.00"));

        // when
        stockService.register(request);

        // then
        Stock stock = stockRepository.findByCode("AAPL").orElseThrow();
        assertThat(stock.getCode()).isEqualTo("AAPL");
        assertThat(stock.getName()).isEqualTo("Apple");
        assertThat(stock.getPrice()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("이미 존재하는 주식을 등록하면 예외가 발생한다.")
    void registerDuplicateStock() {
        // given
        stockRepository.save(Stock.of("TSLA", "Tesla", new BigDecimal("700.00")));
        StockRequest request = StockRequest.of("TSLA", "Tesla", new BigDecimal("720.00"));

        // when // then
        assertThatThrownBy(() -> stockService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock code already exists");
    }

    @Test
    @DisplayName("등록된 주식의 가격을 업데이트한다.")
    void updatePrice() {
        // given
        stockRepository.save(Stock.of("TSLA", "Tesla", new BigDecimal("800.00")));

        // when
        stockService.updatePrice("TSLA", new BigDecimal("820.00"));

        // then
        Stock updatedStock = stockRepository.findByCode("TSLA").orElseThrow();
        assertThat(updatedStock.getPrice()).isEqualByComparingTo("820.00");
    }

    @Test
    @DisplayName("존재하지 않는 주식 코드를 업데이트하려 하면 예외가 발생한다.")
    void updatePriceWithNoStockCode() {
        // when // then
        assertThatThrownBy(() -> stockService.updatePrice("INVALID", new BigDecimal("999.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock not found.");
    }

    @Test
    @DisplayName("등록된 주식이 11개일 때 하나를 삭제할 수 있다.")
    void delete() {
        // given
        IntStream.rangeClosed(1, 11).forEach(i ->
                stockRepository.save(Stock.of("CODE" + i, "Stock " + i, new BigDecimal("100.00")))
        );

        // when
        stockService.delete("CODE11");

        // then
        assertThat(stockRepository.existsByCode("CODE11")).isFalse();
        assertThat(stockRepository.count()).isEqualTo(10);
    }

    @Test
    @DisplayName("등록된 주식이 10개 이하이면 삭제 시 예외가 발생한다.")
    void deleteWithUnder10Stock() {
        // given
        IntStream.rangeClosed(1, 10).forEach(i ->
                stockRepository.save(Stock.of("CODE" + i, "Stock " + i, new BigDecimal("100.00")))
        );

        // when // then
        assertThatThrownBy(() -> stockService.delete("CODE10"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("At least 10 stocks must be registered.");
    }
}