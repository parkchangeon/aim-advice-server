package com.aim.advice.service;

import com.aim.advice.domain.user.Role;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.user.SignupRequest;
import com.aim.advice.dto.user.SignupResponse;
import com.aim.advice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String userId = request.getUserId();
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("UserId already taken");
        }
        String encoded = passwordEncoder.encode(request.getPassword());
        User user = User.of(userId, encoded);
        User saved = userRepository.save(user);
        return SignupResponse.of(saved.getNo(), saved.getUserId());
    }

    @Transactional
    public void updateRole(String userId, Role role) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateRole(role);
    }
}
