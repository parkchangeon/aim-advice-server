package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.advice.RiskType;
import com.aim.advice.domain.balance.TransactionType;
import com.aim.advice.domain.stock.Stock;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import com.aim.advice.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class AdviceServiceTest extends IntegrationTestSupport {

    @Autowired
    private AdviceService adviceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AdviceRepository adviceRepository;

    @Autowired
    private AdviceStockRepository adviceStockRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;


    @Test
    @DisplayName("위험도 HIGH로 자문을 요청하면 잔고 전체를 투자한다.")
    void requestAdviceWithHighRisk() {
        // given
        User user = userRepository.save(User.of("user1", "password", new BigDecimal("100000.00")));
        stockRepository.saveAll(List.of(
                Stock.of("SA", "StockA", new BigDecimal("1000.00")),
                Stock.of("SB", "StockB", new BigDecimal("2000.00"))
        ));
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when
        AdviceResponse response = adviceService.requestAdvice("user1", request);

        // then
        assertThat(response.getInvestedAmount()).isEqualByComparingTo("100000.00");
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("0.00");
        assertThat(user.getBalance()).isEqualByComparingTo("0.00");
        assertThat(response.getInvestedStocks()).isNotEmpty();
        adviceRepository.findAll().forEach(advice -> {
            assertThat(advice.getUser()).isEqualTo(user);
            assertThat(advice.getRiskType()).isEqualTo(request.getRiskType());
            assertThat(advice.getInvestedAmount()).isEqualByComparingTo("100000.00");
            assertThat(advice.getRemainingBalance()).isEqualByComparingTo("0.00");
        });
        adviceStockRepository.findAll().forEach(adviceStock -> {
            assertThat(adviceStock.getAdvice()).isNotNull();
            assertThat(adviceStock.getStock()).isNotNull();
            assertThat(adviceStock.getQuantity()).isGreaterThanOrEqualTo(1);
        });
        balanceHistoryRepository.findAll().forEach(balanceHistory -> {
            assertThat(balanceHistory.getUser()).isEqualTo(user);
            assertThat(balanceHistory.getType()).isEqualTo(TransactionType.WITHDRAWAL);
            assertThat(balanceHistory.getAmount()).isEqualByComparingTo("100000.00");
        });
    }

    @Test
    @DisplayName("위험도 MEDIUM으로 자문을 요청하면 잔고의 절반만 투자한다.")
    void requestAdviceWithMediumRisk() {
        // given
        User user = userRepository.save(User.of("user1", "password", new BigDecimal("100000.00")));
        stockRepository.saveAll(List.of(
                Stock.of("SA", "StockA", new BigDecimal("1000.00")),
                Stock.of("SB", "StockB", new BigDecimal("2000.00"))
        ));
        AdviceRequest request = AdviceRequest.of(RiskType.MEDIUM);

        // when
        AdviceResponse response = adviceService.requestAdvice("user1", request);

        // then
        assertThat(response.getInvestedAmount()).isEqualByComparingTo("50000.00");
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("50000.00");
        assertThat(user.getBalance()).isEqualByComparingTo("50000.00");
        assertThat(response.getInvestedStocks()).isNotEmpty();
        adviceRepository.findAll().forEach(advice -> {
            assertThat(advice.getUser()).isEqualTo(user);
            assertThat(advice.getRiskType()).isEqualTo(request.getRiskType());
            assertThat(advice.getInvestedAmount()).isEqualByComparingTo("50000.00");
            assertThat(advice.getRemainingBalance()).isEqualByComparingTo("50000.00");
        });
        adviceStockRepository.findAll().forEach(adviceStock -> {
            assertThat(adviceStock.getAdvice()).isNotNull();
            assertThat(adviceStock.getStock()).isNotNull();
            assertThat(adviceStock.getQuantity()).isGreaterThanOrEqualTo(1);
        });
        balanceHistoryRepository.findAll().forEach(balanceHistory -> {
            assertThat(balanceHistory.getUser()).isEqualTo(user);
            assertThat(balanceHistory.getType()).isEqualTo(TransactionType.WITHDRAWAL);
            assertThat(balanceHistory.getAmount()).isEqualByComparingTo("50000.00");
        });
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 자문을 요청하면 예외가 발생한다.")
    void requestAdviceWithUserNotFound() {
        // given
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when // then
        assertThatThrownBy(() -> adviceService.requestAdvice("unknown_user", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("잔고가 없는 상태에서 자문을 요청하면 예외가 발생한다.")
    void requestAdviceWithNoBalance() {
        // given
        User user = userRepository.save(User.of("user1", "password", BigDecimal.ZERO));
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when // then
        assertThatThrownBy(() -> adviceService.requestAdvice("user1", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient balance to request portfolio advice");
    }

    @Test
    @DisplayName("자문 요청 시 증권 가격이 잔고보다 높으면 예외가 발생한다.")
    void requestAdviceWithLessBalanceThanStockPrice() {
        // given
        User user = userRepository.save(User.of("user1", "password", new BigDecimal("100.00")));
        stockRepository.saveAll(List.of(
                Stock.of("SA", "StockA", new BigDecimal("1000.00")),
                Stock.of("SB", "StockB", new BigDecimal("2000.00"))
        ));
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when // then
        assertThatThrownBy(() -> adviceService.requestAdvice("user1", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No stocks can be bought with the given budget");
    }

}