package com.aim.advice.domain.advice;

import com.aim.advice.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "advice_stock")
public class AdviceStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advice_no", nullable = false)
    private Advice advice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_no", nullable = false)
    private Stock stock;

    private int quantity;

    @Builder
    private AdviceStock(Advice advice, Stock stock, int quantity) {
        this.advice = advice;
        this.stock = stock;
        this.quantity = quantity;
    }

    public static AdviceStock of(Advice advice, Stock stock, int quantity) {
        return AdviceStock.builder()
                .advice(advice)
                .stock(stock)
                .quantity(quantity)
                .build();
    }
}
