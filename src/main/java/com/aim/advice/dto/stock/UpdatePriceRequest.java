package com.aim.advice.dto.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UpdatePriceRequest {
    @NotBlank(message = "Stock code is required")
    private String code;

    @NotNull(message = "Stock price is required")
    @Min(value = 1, message = "Stock price must be greater than or equal to 1")
    private BigDecimal price;

    @Builder
    private UpdatePriceRequest(String code, BigDecimal price) {
        this.code = code;
        this.price = price;
    }

    public static UpdatePriceRequest of(String code, BigDecimal price) {
        return UpdatePriceRequest.builder()
                .code(code)
                .price(price)
                .build();
    }
}
