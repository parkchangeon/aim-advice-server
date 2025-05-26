package com.aim.advice.config;

import com.aim.advice.security.JwtAuthenticationFilter;
import com.aim.advice.security.JwtUtil;
import com.aim.advice.security.RestAccessDeniedHandler;
import com.aim.advice.security.RestAuthenticationEntryPoint;
import com.aim.advice.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtUtil jwtUtil,
            AuthService authService
    ) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            // H2 콘솔을 iframe으로 띄우기 위해 frameOptions 비활성화
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/v1/users/signup",
                            "/api/v1/auth/login",
                            "/h2-console/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**"
                    ).permitAll()
                    .requestMatchers(
                            "/api/v1/balance/**",
                            "/api/v1/advice/**",
                            "/api/v1/users/role",
                            "/api/v1/stocks/**",
                            "/api/v1/auth/logout"
                    ).authenticated()
                    .anyRequest().denyAll()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .accessDeniedHandler(new RestAccessDeniedHandler())
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, authService),
                    UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }
}
