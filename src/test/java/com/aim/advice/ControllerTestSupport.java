package com.aim.advice;

import com.aim.advice.config.WebSecurityConfig;
import com.aim.advice.controller.AuthController;
import com.aim.advice.controller.BalanceController;
import com.aim.advice.controller.UserController;
import com.aim.advice.security.JwtUtil;
import com.aim.advice.service.AuthService;
import com.aim.advice.service.BalanceService;
import com.aim.advice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class,
        AuthController.class,
        BalanceController.class
})
@Import(WebSecurityConfig.class)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected BalanceService balanceService;

    @MockitoBean
    protected JwtUtil jwtUtil;
}
