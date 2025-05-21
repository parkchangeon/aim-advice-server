package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.BalanceHistory;
import com.aim.advice.domain.User;
import com.aim.advice.domain.TransactionType;
import com.aim.advice.repository.BalanceHistoryRepository;
import com.aim.advice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class BalanceServiceTest extends IntegrationTestSupport {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;

    @Test
    @DisplayName("주어진 금액만큼 잔액이 증가하고 히스토리가 저장된다.")
    void deposit() {
        // given
        userRepository.save(User.of("user1", "pass123"));
        BigDecimal depositAmount = new BigDecimal("100.00");

        // when
        BigDecimal newBalance = balanceService.deposit("user1", depositAmount);

        // then
        assertThat(newBalance).isEqualByComparingTo(depositAmount);

        User updatedUser = userRepository.findByUserId("user1").get();
        assertThat(updatedUser.getBalance()).isEqualByComparingTo(depositAmount);

        List<BalanceHistory> histories = balanceHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        BalanceHistory history = histories.get(0);
        assertThat(history.getUser().getUserId()).isEqualTo("user1");
        assertThat(history.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(history.getAmount()).isEqualByComparingTo(depositAmount);
    }
    
    @Test
    @DisplayName("user가 존재하지 않을 때 IllegalArgumentException이 발생한다.")
    void depositWithNoUser() {
        // when // then
        assertThatThrownBy(() -> balanceService.deposit("nonexistentUser", new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("충분한 잔액이 있을 때 금액만큼 잔액이 감소하고 히스토리가 저장된다.")
    void withdraw() {
        // given
        User user = userRepository.save(User.of("user1", "pass123", new BigDecimal("200.00")));
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        // when
        BigDecimal newBalance = balanceService.withdraw("user1", withdrawAmount);

        // then
        assertThat(newBalance).isEqualByComparingTo(new BigDecimal("150.00"));

        User updatedUser = userRepository.findByUserId("user1").get();
        assertThat(updatedUser.getBalance()).isEqualByComparingTo(new BigDecimal("150.00"));

        List<BalanceHistory> histories = balanceHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        BalanceHistory history = histories.get(0);
        assertThat(history.getUser().getUserId()).isEqualTo("user1");
        assertThat(history.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(history.getAmount()).isEqualByComparingTo(withdrawAmount);
    }



    @Test
    @DisplayName("user가 존재하지 않을 때 IllegalArgumentException이 발생한다.")
    void withdrawWithNoUser() {
        // when // then
        assertThatThrownBy(() -> balanceService.withdraw("nonexistentUser", new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("잔액이 부족할 때 IllegalArgumentException이 발생한다.")
    void withdrawWithInsufficientBalance() {
        // given
        userRepository.save(User.of("user1", "pass123", new BigDecimal("30.00")));
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        // when // then
        assertThatThrownBy(() -> balanceService.withdraw("user1", withdrawAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient balance");

        assertThat(balanceHistoryRepository.findAll()).isEmpty();
    }

}