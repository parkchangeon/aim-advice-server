package com.aim.advice.service;

import com.aim.advice.domain.advice.Advice;
import com.aim.advice.domain.advice.AdviceStock;
import com.aim.advice.domain.advice.RiskType;
import com.aim.advice.domain.balance.BalanceHistory;
import com.aim.advice.domain.stock.Stock;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import com.aim.advice.dto.advice.InvestedStock;
import com.aim.advice.repository.*;
import com.aim.advice.util.PortfolioOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.aim.advice.domain.balance.TransactionType.WITHDRAWAL;

@Service
@RequiredArgsConstructor
public class AdviceService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final AdviceRepository adviceRepository;
    private final AdviceStockRepository adviceStockRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    @Transactional
    public AdviceResponse requestAdvice(String userId, AdviceRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal balance = user.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Insufficient balance to request portfolio advice");
        }
        BigDecimal investAmount = null;

        RiskType riskType = request.getRiskType();
        if (riskType == RiskType.HIGH) {
            investAmount = balance;
        } else if (riskType == RiskType.MEDIUM) {
            investAmount = balance.multiply(BigDecimal.valueOf(0.5));
        }

        List<Stock> stocks = stockRepository.findAll();
        List<InvestedStock> investedStocks = PortfolioOptimizer.optimize(investAmount, stocks);
        BigDecimal remaining = withDrawBalance(user, investAmount);
        saveAdvice(user, investAmount, riskType, stocks, investedStocks, remaining);

        return AdviceResponse.of(investAmount, remaining, investedStocks);
    }

    private BigDecimal withDrawBalance(User user, BigDecimal investAmount) {
        BigDecimal newBalance = user.withdraw(investAmount);
        balanceHistoryRepository.save(BalanceHistory.of(user, WITHDRAWAL, investAmount));
        return newBalance;
    }

    private void saveAdvice(User user, BigDecimal investAmount, RiskType riskType, List<Stock> stocks, List<InvestedStock> investedStocks, BigDecimal remaining) {
        Advice advice = adviceRepository.save(
                Advice.of(user, investAmount, remaining, riskType)
        );

        List<AdviceStock> adviceStocks = investedStocks.stream()
                .map(is -> {
                    Stock stock = stocks.stream()
                            .filter(s -> s.getCode().equals(is.getCode()))
                            .findFirst()
                            .orElseThrow();
                    return AdviceStock.of(advice, stock, is.getQuantity());
                })
                .toList();

        adviceStockRepository.saveAll(adviceStocks);
    }
}
