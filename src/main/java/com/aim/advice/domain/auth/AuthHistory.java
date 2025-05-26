package com.aim.advice.domain.auth;

import com.aim.advice.domain.CreatedEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "auth_history")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@Getter
public class AuthHistory extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String action;

    @Builder
    private AuthHistory(String userId, String action) {
        this.userId = userId;
        this.action = action;
    }

    public static AuthHistory of(String userId, String action) {
        return AuthHistory.builder()
                .userId(userId)
                .action(action)
                .build();
    }
}
