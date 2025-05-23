package com.aim.advice.domain.stock;

import com.aim.advice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    public void updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
    }

    @Builder
    private Stock(Long no, String code, String name, BigDecimal price) {
        this.no = no;
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public static Stock of(String code, String name, BigDecimal price) {
        return Stock.builder()
                .code(code)
                .name(name)
                .price(price)
                .build();
    }

    public static Stock of(Long no, String code, String name, BigDecimal price) {
        return Stock.builder()
                .no(no)
                .code(code)
                .name(name)
                .price(price)
                .build();
    }
}
