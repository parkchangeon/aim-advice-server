package com.aim.advice.dto.advice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class InvestedStock {
    private String code;
    private String name;
    private int quantity;
    private BigDecimal price;

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
