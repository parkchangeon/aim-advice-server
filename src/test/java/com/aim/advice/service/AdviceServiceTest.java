package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.advice.RiskType;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import com.aim.advice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class AdviceServiceTest extends IntegrationTestSupport {

    @Autowired
    private AdviceService adviceService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("위험도 HIGH로 자문을 요청하면 잔고 전체를 투자한다.")
    @Test
    void requestAdviceWithHighRisk() {
        // given
        User user = userRepository.save(User.of("user1", "password", new BigDecimal("100000.00")));
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when
        AdviceResponse response = adviceService.requestAdvice("user1", request);

        // then
        assertThat(response.getInvestedAmount()).isEqualByComparingTo("100000.00");
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("0.00");
        assertThat(user.getBalance()).isEqualByComparingTo("0.00");
    }

    @DisplayName("위험도 MEDIUM으로 자문을 요청하면 잔고의 절반만 투자한다.")
    @Test
    void requestAdviceWithMediumRisk() {
        // given
        User user = userRepository.save(User.of("user1", "password", new BigDecimal("100000.00")));
        AdviceRequest request = AdviceRequest.of(RiskType.MEDIUM);

        // when
        AdviceResponse response = adviceService.requestAdvice("user1", request);

        // then
        assertThat(response.getInvestedAmount()).isEqualByComparingTo("50000.00");
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("50000.00");
        assertThat(user.getBalance()).isEqualByComparingTo("50000.00");
    }

    @DisplayName("존재하지 않는 사용자로 자문을 요청하면 예외가 발생한다.")
    @Test
    void requestAdviceWithUserNotFound() {
        // given
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when // then
        assertThatThrownBy(() -> adviceService.requestAdvice("unknown_user", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @DisplayName("잔고가 없는 상태에서 자문을 요청하면 예외가 발생한다.")
    @Test
    void requestAdviceWithNoBalance() {
        // given
        User user = userRepository.save(User.of("user1", "password", BigDecimal.ZERO));
        AdviceRequest request = AdviceRequest.of(RiskType.HIGH);

        // when // then
        assertThatThrownBy(() -> adviceService.requestAdvice("user1", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient balance to request portfolio advice");
    }

}