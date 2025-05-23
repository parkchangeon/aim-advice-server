package com.aim.advice.config;

import com.aim.advice.domain.stock.Stock;
import com.aim.advice.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class StockInitializer {

    private final StockRepository stockRepository;

    @PostConstruct
    public void initStocks() {
        if (stockRepository.count() >= 10) return;

        List<Stock> defaultStocks = List.of(
                Stock.of("AAPL", "Apple", new BigDecimal("150.00")),
                Stock.of("TSLA", "Tesla", new BigDecimal("800.00")),
                Stock.of("GOOGL", "Google", new BigDecimal("2500.00")),
                Stock.of("AMZN", "Amazon", new BigDecimal("3100.00")),
                Stock.of("MSFT", "Microsoft", new BigDecimal("290.00")),
                Stock.of("NFLX", "Netflix", new BigDecimal("550.00")),
                Stock.of("NVDA", "Nvidia", new BigDecimal("190.00")),
                Stock.of("META", "Meta", new BigDecimal("340.00")),
                Stock.of("INTC", "Intel", new BigDecimal("45.00")),
                Stock.of("AMD", "AMD", new BigDecimal("115.00"))
        );

        stockRepository.saveAll(defaultStocks);
    }
}
