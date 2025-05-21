package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.dto.user.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("신규 회원가입을 등록한다.")
    @Test
    void createUser() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("testuser", "validPass1");

        // when // then
        mockMvc.perform(
                        post("/api/v1/users/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("회원가입 시 userId는 필수이다.")
    @Test
    void createUserWithEmptyUserId() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("", "validPass1");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("UserId is required"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 시 userId는 4자리 이상이어야 한다.")
    @Test
    void createUserWithUserIdSizeMoreThan4() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("abc", "validPass1");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("userId size must be between 4 and 16"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 시 userId는 16자리 이하여야 한다.")
    @Test
    void createUserWithUserIdSizeLessThan16() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("abcdefghijklmnopq", "validPass1");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("userId size must be between 4 and 16"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 시 password는 필수이다.")
    @Test
    void createUserWithEmptyPassword() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("test", "");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Password is required"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 시 password는 8자리 이상이어야 한다.")
    @Test
    void createUserWithPasswordSizeMoreThan8() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("test", "validPa");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("password size must be between 8 and 16"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 시 password는 16자리 이하여야 한다.")
    @Test
    void createUserWithPasswordSizeLessThan16() throws Exception {
        // given
        SignupRequest request = SignupRequest.of("test", "validPass12345678");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("password size must be between 8 and 16"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}