package com.aim.advice.service;

import com.aim.advice.domain.BalanceHistory;
import com.aim.advice.domain.User;
import com.aim.advice.repository.BalanceHistoryRepository;
import com.aim.advice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.aim.advice.domain.TransactionType.DEPOSIT;
import static com.aim.advice.domain.TransactionType.WITHDRAWAL;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final UserRepository userRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    @Transactional
    public BigDecimal deposit(String userId, BigDecimal amount) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = user.deposit(amount);
        balanceHistoryRepository.save(BalanceHistory.of(user, DEPOSIT, amount));
        return newBalance;
    }

    @Transactional
    public BigDecimal withdraw(String userId, BigDecimal amount) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BigDecimal newBalance = user.withdraw(amount);
        balanceHistoryRepository.save(BalanceHistory.of(user, WITHDRAWAL, amount));
        return newBalance;
    }
}