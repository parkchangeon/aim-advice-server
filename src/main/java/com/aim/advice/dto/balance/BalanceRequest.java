package com.aim.advice.dto.balance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BalanceRequest {
    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be greater than or equal to 1")
    private BigDecimal amount;

    @Builder
    private BalanceRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public static BalanceRequest of(BigDecimal amount) {
        return BalanceRequest.builder()
                .amount(amount)
                .build();
    }
}
