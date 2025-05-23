package com.aim.advice;

import com.aim.advice.config.WebSecurityConfig;
import com.aim.advice.controller.*;
import com.aim.advice.security.JwtUtil;
import com.aim.advice.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class,
        AuthController.class,
        BalanceController.class,
        AdviceController.class,
        StockController.class
})
@Import(WebSecurityConfig.class)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtUtil jwtUtil;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected BalanceService balanceService;

    @MockitoBean
    protected AdviceService adviceService;

    @MockitoBean
    protected StockService stockService;

}
