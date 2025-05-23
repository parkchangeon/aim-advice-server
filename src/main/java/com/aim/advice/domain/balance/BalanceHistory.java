package com.aim.advice.domain.balance;

import com.aim.advice.domain.CreatedEntity;
import com.aim.advice.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance_history")
public class BalanceHistory extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Builder
    private BalanceHistory(User user, TransactionType type, BigDecimal amount) {
        this.user = user;
        this.type = type;
        this.amount = amount;
    }

    public static BalanceHistory of(User user, TransactionType type, BigDecimal amount) {
        return BalanceHistory.builder()
                .user(user)
                .type(type)
                .amount(amount)
                .build();
    }
}