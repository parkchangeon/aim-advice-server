package com.aim.advice.domain.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String text;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
