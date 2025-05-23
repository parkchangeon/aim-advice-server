package com.aim.advice.dto.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class StockRequest {
    @NotBlank(message = "Stock code is required")
    private String code;

    @NotBlank(message = "Stock name is required")
    private String name;

    @NotNull(message = "Stock price is required")
    @Min(value = 1, message = "Stock price must be greater than or equal to 1")
    private BigDecimal price;

    @Builder
    private StockRequest(String code, String name, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public static StockRequest of(String code, String name, BigDecimal price) {
        return StockRequest.builder()
                .code(code)
                .name(name)
                .price(price)
                .build();
    }
}
