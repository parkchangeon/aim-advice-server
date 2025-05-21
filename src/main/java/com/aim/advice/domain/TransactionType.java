package com.aim.advice.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금");

    private final String text;
}
