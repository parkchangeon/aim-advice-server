package com.aim.advice.service;

import com.aim.IntegrationTestSupport;
import com.aim.advice.domain.user.Role;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.user.SignupRequest;
import com.aim.advice.dto.user.SignupResponse;
import com.aim.advice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("아이디와 비밀번호를 받아 회원가입을 한다.")
    @Test
    void createUser() {
        // given
        SignupRequest signupRequest = SignupRequest.of("test", "1234");

        // when
        SignupResponse signupResponse = userService.signup(signupRequest);

        // then
        assertThat(signupResponse.getNo()).isNotNull();
        assertThat(signupResponse.getUserId()).isEqualTo("test");
    }

    @DisplayName("기존 아이디로 회원가입을 시도하면 예외가 발생한다.")
    @Test
    void createUserWithDuplicateUsername() {
        // given
        SignupRequest signupRequest = SignupRequest.of("test", "1234");
        userService.signup(signupRequest);

        // when // then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId already taken");
    }

    @DisplayName("회원 권한을 변경한다.")
    @Test
    void updateRole() {
        // given
        userRepository.save(User.of("user1", "password"));

        // when
        userService.updateRole("user1", Role.ADMIN);

        // then
        User user = userRepository.findByUserId("user1").orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @DisplayName("회원 권한을 변경할 때 회원 아이디가 존재하지 않으면 예외가 발생한다.")
    @Test
    void updateRoleWithNoUser() {
        // when // then
        assertThatThrownBy(() -> userService.updateRole("user1", Role.ADMIN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}