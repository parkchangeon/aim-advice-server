package com.aim.advice.dto.advice;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvestedStock {
    private final String code;
    private final String name;
    private final int quantity;
    private final BigDecimal price;

    @Builder
    private InvestedStock(String code, String name, int quantity, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public static InvestedStock of(String code, String name, int quantity, BigDecimal price) {
        return InvestedStock.builder()
                .code(code)
                .name(name)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
