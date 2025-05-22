package com.aim.advice.domain.user;

import com.aim.advice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    public BigDecimal deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

    @Builder
    private User(Long no, String userId, String password, BigDecimal balance) {
        this.no = no;
        this.userId = userId;
        this.password = password;
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
    }

    public static User of(String userId, String password) {
        return User.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public static User of(String userId, String password, BigDecimal balance) {
        return User.builder()
                .userId(userId)
                .password(password)
                .balance(balance)
                .build();
    }
}
