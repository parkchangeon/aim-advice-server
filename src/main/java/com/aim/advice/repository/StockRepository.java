package com.aim.advice.repository;

import com.aim.advice.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    boolean existsByCode(String code);

    Optional<Stock> findByCode(String code);

    long count();
}
