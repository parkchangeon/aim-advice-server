package com.aim.advice.domain.advice;

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
@Table(name = "advice")
public class Advice extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal investedAmount;

    @Column(nullable = false)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskType riskType;

    @Builder
    private Advice(User user, BigDecimal investedAmount, BigDecimal remainingBalance, RiskType riskType) {
        this.user = user;
        this.investedAmount = investedAmount;
        this.remainingBalance = remainingBalance;
        this.riskType = riskType;
    }

    public static Advice of(User user, BigDecimal investedAmount, BigDecimal remainingBalance, RiskType riskType) {
        return Advice.builder()
                .user(user)
                .investedAmount(investedAmount)
                .remainingBalance(remainingBalance)
                .riskType(riskType)
                .build();
    }
}
