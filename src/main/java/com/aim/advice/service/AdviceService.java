package com.aim.advice.service;

import com.aim.advice.domain.advice.RiskType;
import com.aim.advice.domain.user.User;
import com.aim.advice.dto.advice.AdviceRequest;
import com.aim.advice.dto.advice.AdviceResponse;
import com.aim.advice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdviceService {

    private final UserRepository userRepository;

    @Transactional
    public AdviceResponse requestAdvice(String userId, AdviceRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal balance = user.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Insufficient balance to request portfolio advice");
        }
        BigDecimal investAmount = null;

        if (request.getRiskType() == RiskType.HIGH) {
            investAmount = balance;
        } else if (request.getRiskType() == RiskType.MEDIUM) {
            investAmount = balance.multiply(BigDecimal.valueOf(0.5));
        }
        BigDecimal remaining = user.withdraw(investAmount);

        return AdviceResponse.of(investAmount, remaining);
    }
}
