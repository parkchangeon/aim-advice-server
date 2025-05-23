package com.aim.advice.controller;

import com.aim.advice.ControllerTestSupport;
import com.aim.advice.domain.user.Role;
import com.aim.advice.dto.user.SignupRequest;
import com.aim.advice.dto.user.UpdateRoleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
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

    @Test
    @DisplayName("관리자가 사용자의 역할을 변경할 수 있다.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRole() throws Exception {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.of("user1", Role.ADMIN);

        // when // then
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("관리자가 아닌 경우 사용자의 역할을 변경할 수 없다.")
    @WithMockUser(username = "user")
    void updateRoleWithNoAdmin() throws Exception {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.of("user1", Role.ADMIN);

        // when // then
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @DisplayName("관리자가 사용자의 역할을 변경할 때 변경할 userId 값은 필수이다.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRoleWithNoUserId() throws Exception {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.of("", Role.ADMIN);

        // when // then
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("UserId is required"));
    }

    @Test
    @DisplayName("관리자가 사용자의 역할을 변경할 때 변경할 role 값은 필수이다.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRoleWithNoUserRole() throws Exception {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.of("user1", null);

        // when // then
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Role is required"));
    }

    @Test
    @DisplayName("올바른 role 유형으로 role 변경 요청을 해야 한다.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRoleWithRightRole() throws Exception {
        // given
        String invalidJson = """
                    {
                      "userId": "user1",
                      "role": "INVALID"
                    }
                """;

        // when // then
        mockMvc.perform(put("/api/v1/users/role")
                        .with(csrf())
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

    @Test
    @DisplayName("role 변경할 때 request body가 아예 없는 경우 변경할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void updateRoleWithNoRequestBody() throws Exception {
        // when // then
        mockMvc.perform(
                        put("/api/v1/users/role")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed"));
    }

}