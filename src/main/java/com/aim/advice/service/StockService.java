package com.aim.advice.service;

import com.aim.advice.domain.stock.Stock;
import com.aim.advice.dto.stock.StockRequest;
import com.aim.advice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void register(StockRequest request) {
        if (stockRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Stock code already exists");
        }

        Stock stock = Stock.of(request.getCode(), request.getName(), request.getPrice());
        stockRepository.save(stock);
    }

    @Transactional
    public void updatePrice(String stockCode, BigDecimal price) {
        Stock stock = getStock(stockCode);
        stock.updatePrice(price);
    }

    @Transactional
    public void delete(String stockCode) {
        Stock stock = getStock(stockCode);

        if (stockRepository.count() <= 10) {
            throw new IllegalStateException("At least 10 stocks must be registered.");
        }

        stockRepository.delete(stock);
    }

    private Stock getStock(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found."));
        return stock;
    }
}
