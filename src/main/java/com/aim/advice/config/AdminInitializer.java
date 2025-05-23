package com.aim.advice.config;

import com.aim.advice.domain.user.Role;
import com.aim.advice.domain.user.User;
import com.aim.advice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByUserId("admin")) {
            userRepository.save(User.of("admin", passwordEncoder.encode("admin1234"), Role.ADMIN));
        }
    }
}