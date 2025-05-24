package com.aim.advice.domain.balance;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    INQUIRY("조회");

    private final String text;
}
