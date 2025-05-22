package com.aim.advice.repository;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.auth.AuthHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class AuthHistoryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private AuthHistoryRepository authHistoryRepository;

    @DisplayName("userId와 action 값으로 저장된 로그인 이력을 조회한다.")
    @Test
    void findByUserIdAndAction() {
        // given
        AuthHistory h1 = AuthHistory.of("user1", "LOGIN");
        AuthHistory h2 = AuthHistory.of("user1", "LOGOUT");
        AuthHistory h3 = AuthHistory.of("user1", "LOGIN");
        authHistoryRepository.saveAll(List.of(h1, h2, h3));

        // when
        List<AuthHistory> result = authHistoryRepository.findByUserIdAndAction("user1", "LOGIN");

        // then
        assertThat(result)
                .hasSize(2)
                .extracting(AuthHistory::getUserId, AuthHistory::getAction)
                .containsExactly(
                        tuple("user1", "LOGIN")
                        , tuple("user1", "LOGIN")
                );
    }

    @DisplayName("조건에 맞는 이력이 없으면 빈 리스트를 반환한다")
    @Test
    void findByUserIdAndActionWithNoMatch() {
        // given
        AuthHistory authHistory = AuthHistory.of("user1", "LOGIN");
        authHistoryRepository.save(authHistory);

        // when
        List<AuthHistory> result = authHistoryRepository.findByUserIdAndAction("user1", "LOGOUT");

        // then
        assertThat(result).isEmpty();
    }
}