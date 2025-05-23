package com.aim.advice.repository;

import com.aim.advice.domain.advice.AdviceStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdviceStockRepository extends JpaRepository<AdviceStock, Long> {
}
