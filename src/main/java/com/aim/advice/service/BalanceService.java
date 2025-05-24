package com.aim.advice.service;

import com.aim.advice.domain.balance.BalanceHistory;
import com.aim.advice.domain.user.User;
import com.aim.advice.repository.BalanceHistoryRepository;
import com.aim.advice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.aim.advice.domain.balance.TransactionType.*;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final UserRepository userRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    @Transactional
    public BigDecimal deposit(String userId, BigDecimal amount) {
        User user = findUser(userId);
        BigDecimal newBalance = user.deposit(amount);
        balanceHistoryRepository.save(BalanceHistory.of(user, DEPOSIT, amount));
        return newBalance;
    }

    @Transactional
    public BigDecimal withdraw(String userId, BigDecimal amount) {
        User user = findUser(userId);
        BigDecimal newBalance = user.withdraw(amount);
        balanceHistoryRepository.save(BalanceHistory.of(user, WITHDRAWAL, amount));
        return newBalance;
    }

    public BigDecimal inquireBalance(String userId) {
        User user = findUser(userId);
        BigDecimal balance = user.getBalance();
        balanceHistoryRepository.save(BalanceHistory.of(user, INQUIRY, balance));
        return balance;
    }

    private User findUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}