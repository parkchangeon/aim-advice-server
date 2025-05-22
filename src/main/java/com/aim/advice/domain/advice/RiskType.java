package com.aim.advice.domain.advice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RiskType {
    HIGH("유형1"),
    MEDIUM("유형2");

    private final String text;
}
