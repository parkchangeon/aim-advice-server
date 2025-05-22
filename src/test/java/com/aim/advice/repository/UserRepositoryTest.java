package com.aim.advice.repository;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("주어진 userId가 존재할 때 existsByUserId는 true를 반환한다.")
    void existsByUserId_whenUserExists_thenTrue() {
        // given
        User user = User.builder()
                .userId("testuser")
                .password("password123")
                .build();
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUserId("testuser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("주어진 userId가 존재하지 않을 때 existsByUserId는 false를 반환한다.")
    void existsByUserId_whenUserNotExists_thenFalse() {
        // when
        boolean exists = userRepository.existsByUserId("no_user");

        // then
        assertThat(exists).isFalse();
    }
}