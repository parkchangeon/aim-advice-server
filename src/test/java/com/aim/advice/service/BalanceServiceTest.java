package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.balance.BalanceHistory;
import com.aim.advice.domain.balance.TransactionType;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.balance.BalanceResponse;
import com.aim.advice.repository.BalanceHistoryRepository;
import com.aim.advice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceServiceTest extends IntegrationTestSupport {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;

    @Test
    @DisplayName("입금할 때 주어진 금액만큼 잔액이 증가하고 히스토리가 저장된다.")
    void deposit() {
        // given
        userRepository.save(User.of("user1", "pass123"));
        BigDecimal depositAmount = new BigDecimal("100.00");

        // when
        BalanceResponse response = balanceService.deposit("user1", depositAmount);

        // then
        assertThat(response.getBalance()).isEqualByComparingTo(depositAmount);

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
        BalanceResponse response = balanceService.withdraw("user1", withdrawAmount);

        // then
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("150.00"));

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

    @Test
    @DisplayName("사용자의 잔고를 조회하면 잔고를 반환하고 조회 내역을 저장한다.")
    void inquireBalance() {
        // given
        User user = User.of("user1", "pass1234", new BigDecimal("1000.00"));
        userRepository.save(user);

        // when
        BalanceResponse response = balanceService.inquireBalance("user1");

        // then
        assertThat(response.getBalance()).isEqualByComparingTo("1000.00");
        balanceHistoryRepository.findAll().forEach(balanceHistory -> {
            assertThat(balanceHistory.getUser()).isEqualTo(user);
            assertThat(balanceHistory.getType()).isEqualTo(TransactionType.INQUIRY);
            assertThat(balanceHistory.getAmount()).isEqualByComparingTo("1000.00");
        });
    }

    @Test
    @DisplayName("사용자의 잔고를 조회할 때 user가 존재하지 않으면 IllegalArgumentException이 발생한다.")
    void inquireBalanceWithNoUser() {
        // when // then
        assertThatThrownBy(() -> balanceService.inquireBalance("nonexistentUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}